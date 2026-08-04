# Plan de ejecución

Seis fases. F1 es urgente y no depende de nada. El orden entre F3 y F4 es
obligatorio: migrar el esquema con `fallbackToDestructiveMigration`
todavía activo borraría los datos que F1 acaba de salvar.

```
F1 ──► F2 ──► F3 ──► F4
                      ▲
F5 (independiente)    │
F6 (independiente) ───┘  conviene antes de F4
```

---

## F1 — Detener la pérdida de datos

**Urgencia: máxima. Sin dependencias. Sin cambio de esquema.**

Arregla [H1, H2 y H3](hallazgos.md) con el esquema actual, para poder
publicar sin esperar al resto del plan.

### F1.1 — `createNote` escribe en Room siempre

`repository/NotesRepository.kt:74`. Invertir el orden: local primero,
red después.

```kotlin
suspend fun createNote(context: Context, request: CreateNoteRequest): Result<Note> =
    runCatching {
        // 1. Room, incondicional
        val local = buildLocalNote(context, request)
        saveNoteLocally(context, local)

        // 2. Sincronizar si se puede; el fallo no propaga
        if (BackendGate.isReachable()) {
            runCatching { notesService.createNote(request) }
                .onFailure { savePendingRequest(context, "notas", request) }
        } else {
            savePendingRequest(context, "notas", request)
        }

        local
    }
```

`buildLocalNote` genera un id local negativo, que nunca colisiona con los
ids positivos del servidor:

```kotlin
private suspend fun buildLocalNote(context: Context, request: CreateNoteRequest): Note {
    val dao = DatabaseProvider.getDatabase(context).noteDao()
    val nextLocalId = minOf(dao.minId() ?: 0, 0) - 1   // -1, -2, -3…
    val materia = DatabaseProvider.getDatabase(context).materiaDao()
        .getMateriaById(request.idMateria)
    return Note(
        id = nextLocalId,
        score = request.puntaje,
        attempt = dao.getNotes(materia?.name.orEmpty()).size + 1,
        date = System.currentTimeMillis().toString(),
        subjectId = request.idMateria,
        subjectName = materia?.name.orEmpty()
    )
}
```

Requiere añadir a `NoteDao`:
```kotlin
@Query("SELECT MIN(id) FROM note")
suspend fun minId(): Int?
```

> `@Query` añadido a un DAO no cambia el esquema — no hace falta subir la
> versión de `AppDatabase` ni migrar.

**Aceptación:** completar un quiz en modo avión, matar la app, reabrir:
la puntuación está en el historial.

### F1.2 — La cola solo borra tras éxito

`repository/NotesRepository.kt:167`. Sacar el borrado del `finally`.

```kotlin
for (request in pendingRequests) {
    val ok = runCatching {
        notesService.createOfflineNote(Json.decodeFromString(request.payload))
    }.isSuccess

    if (ok) {
        pendingRequestDao.deleteRequestById(request.id)
    } else {
        Log.w("NotesRepository", "Petición ${request.id} sigue pendiente")
    }
}
```

Un payload corrupto sí debe descartarse — no se arregla reintentando:

```kotlin
val payload = runCatching {
    Json.decodeFromString<CreateOfflineNoteRequest>(request.payload)
}.getOrElse {
    Log.e("NotesRepository", "Payload ilegible en ${request.id}, se descarta")
    pendingRequestDao.deleteRequestById(request.id)
    continue
}
```

**Aceptación:** con `BACKEND_ENABLED = true` apuntando a un host
inexistente, las filas de `pending_requests` sobreviven al ciclo.

### F1.3 — Token vacío no genera cabecera

`network/ApiClient.kt:37`.

```kotlin
token?.takeIf { it.isNotBlank() }?.let {
    requestBuilder.addHeader("Authorization", "Bearer $it")
}
```

**Aceptación:** con token `""`, la petición sale sin cabecera
`Authorization`.

### F1.4 — Mensajes honestos

Quitar el `throw` de la rama offline de `createNote`: la operación ahora
sí tiene éxito. `NotesViewModel.addNote` pasa a mostrar
`"Puntuación guardada."` sin mencionar sincronización.

---

## F2 — Interruptor único

**Depende de: F1.**

1. `buildConfigField("boolean", "BACKEND_ENABLED", "false")` en
   `defaultConfig`.
2. Crear `network/BackendGate.kt` — ver
   [estrategia.md](estrategia.md#principio-2--un-solo-interruptor).
3. Sustituir las 5 apariciones de
   `NetworkObserver.isNetworkAvailable.first()` en `NotesRepository` y
   `UserRepository` por `BackendGate.isReachable()`.
4. Ocultar `ChangePasswordSection` en `ui/user/UserScreen.kt:135` tras
   `if (BackendGate.isEnabled)`.
5. Registrar `Routes.LOGIN` condicionalmente en `AppNavigation`, con
   `LoginScreen` restaurado desde la isla latente.
6. Marcar los archivos latentes con el encabezado de
   [estrategia.md](estrategia.md#principio-3--el-código-muerto-se-congela-no-se-borra).
7. Borrar `model/LoginErrorResponse.kt` — cero referencias.

**Aceptación:** con `BACKEND_ENABLED = false`, ninguna petición sale del
dispositivo. Verificable con `read_network_requests` o
`adb shell netstat`. Poniéndolo en `true` reaparecen la ruta de login y
la sección de contraseña.

---

## F3 — Migraciones reales de Room

**Depende de: F2. Bloquea F4.**

1. `exportSchema = true` en `AppDatabase` y añadir
   `room.schemaLocation` a los argumentos de kapt/KSP.
2. Quitar `fallbackToDestructiveMigration()` de
   `DatabaseProvider.kt:22`.
3. Commitear el esquema exportado de la versión 11 como línea base.

> Sin el JSON de la v11 no se puede escribir una migración verificable.
> Este paso es el que hace posible F4.

Para instalaciones existentes cuyo esquema real no coincida con el
exportado, `MIGRATION_11_12` de F4 debe ser tolerante: `ALTER TABLE …
ADD COLUMN` con valores por defecto, nunca `DROP`.

4. `MateriaDao.insertAll` pasa a
   `@Insert(onConflict = OnConflictStrategy.REPLACE)` — la siembra se
   vuelve idempotente.

**Aceptación:** subir `version` a 12 sin migración hace fallar la
compilación o el arranque, en lugar de borrar la base en silencio.

---

## F4 — Esquema de sincronización

**Depende de: F3.**

`Note` versión 12:

```kotlin
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: Int? = null,
    val synced: Boolean = false,
    val score: Int,
    val attempt: Int,
    val date: Long,
    val subjectId: Int,
    val subjectName: String
)
```

```kotlin
val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE note ADD COLUMN remoteId INTEGER")
        db.execSQL("ALTER TABLE note ADD COLUMN synced INTEGER NOT NULL DEFAULT 0")
        // ids negativos de F1 → notas locales sin confirmar
        db.execSQL("UPDATE note SET remoteId = id, synced = 1 WHERE id > 0")
    }
}
```

`date` pasa de `String` a `Long`. Como el formato previo no está
garantizado, la migración no intenta parsearlo: se añade `dateMillis`
nueva con `0` por defecto y se deja `date` como columna heredada, a
retirar en la versión 13 cuando ya no queden filas antiguas.

Con esto, `pending_requests` deja de usarse: la reconciliación pasa a ser
`SELECT * FROM note WHERE synced = 0`. La tabla se retira en la 13.

**Aceptación:** instalar la versión anterior, generar notas, actualizar a
la nueva: las notas siguen ahí, las locales con `synced = 0`.

---

## F5 — Seguridad

**Independiente. Bloquea cualquier publicación.**

Los tres hallazgos críticos de [v1/deuda-tecnica.md](../v1/deuda-tecnica.md):

1. `network/ApiClient.kt:47` — nivel de log condicionado:
   `level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE`.
2. `network/ApiClient.kt:27,35` — borrar los `Log.d` que imprimen el
   token.
3. `network/ApiClient.kt:52` — eliminar `bodyInterceptor` entero;
   `HttpLoggingInterceptor` ya hace eso y sin la `ResponseBody.create`
   deprecada.
4. `app/build.gradle.kts:31` — `signingConfig` real, con la keystore
   fuera del repositorio.

**Aceptación:** `adb logcat` durante una sesión de release no muestra
tokens ni cuerpos de petición.

---

## F6 — Toolchain, SDK y dependencias

**Independiente, pero conviene antes de F4** para no migrar el esquema
dos veces (kapt → KSP cambia la generación de Room).

Detalle completo en [actualizaciones.md](actualizaciones.md).

---

## Fase R — Reactivación del backend

No es una fase del plan: es el procedimiento del día que el servidor
vuelva.

1. `BACKEND_ENABLED = true`.
2. Verificar que `ApiClient.BASE_URL` sigue siendo correcta.
3. Confirmar que el registro local produce un token válido — hoy
   `buildLocalUser` guarda `""` y `AuthRepository.register` no llama a la
   API. Decidir: ¿el registro local migra a cuenta de servidor, o el
   login vuelve a ser obligatorio?
4. Reconciliar: subir las notas con `synced = 0`.
5. Quitar los encabezados `LATENTE` de los archivos reactivados.

El punto 3 es una decisión de producto pendiente, no técnica. Conviene
resolverla antes de que el servidor vuelva, no después.

---

## Resumen de archivos por fase

| Fase | Archivos |
|---|---|
| F1 | `NotesRepository.kt`, `NoteDao.kt`, `ApiClient.kt`, `NotesViewModel.kt` |
| F2 | `build.gradle.kts`, nuevo `BackendGate.kt`, `NotesRepository.kt`, `UserRepository.kt`, `UserScreen.kt`, `AppNavigation.kt`, 7 archivos latentes, borrar `LoginErrorResponse.kt` |
| F3 | `AppDatabase.kt`, `DatabaseProvider.kt`, `MateriaDao.kt`, `build.gradle.kts` |
| F4 | `Note.kt`, `NoteDao.kt`, `NotesRepository.kt`, `NoteMapper.kt`, nuevo `Migrations.kt` |
| F5 | `ApiClient.kt`, `build.gradle.kts` |
| F6 | `libs.versions.toml`, `build.gradle.kts` (×2), `gradle-wrapper.properties`, `gradle.properties` |
