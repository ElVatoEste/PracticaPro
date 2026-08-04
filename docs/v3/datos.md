# Datos

## Esquema v13

`AppDatabase`, `exportSchema = true`, esquemas versionados en `app/schemas/`.
Sin `fallbackToDestructiveMigration`: cada subida de versión exige una
`Migration` explícita.

### `note`

```kotlin
@Entity(tableName = "note")
data class Note(
    @PrimaryKey val id: Int,          // negativo = local, positivo = del servidor
    val remoteId: Int? = null,        // null mientras solo exista en local
    @ColumnInfo(defaultValue = "0") val synced: Boolean = false,
    val score: Int,
    val attempt: Int,
    @ColumnInfo(defaultValue = "0") val dateMillis: Long = 0L,
    val subjectId: Int,
    val subjectName: String
)
```

**El signo del id distingue el origen.** Los ids del servidor son positivos;
`buildLocalNote` genera negativos decrecientes. No hay ambigüedad posible ni
hace falta un campo aparte para saber de dónde vino una fila.

`@ColumnInfo(defaultValue = "0")` no es decorativo: sin él, el esquema
exportado no declara valor por defecto y el `DEFAULT 0` del `ALTER TABLE` de la
migración provoca una discrepancia que Room detecta en runtime.

### `user` y `materia`

`user` es fila única con id `-1` (misma convención de signo). `materia` es el
catálogo, derivado de `MODULOS` para que un módulo nuevo no exija recordar
sembrar su materia.

## Migraciones

| Migración | Qué hace |
|---|---|
| `11 → 12` | Añade `remoteId`, `synced` y `dateMillis`. Marca como sincronizadas las filas con id positivo y parsea `date` cuando es numérica |
| `12 → 13` | Retira `note.date` y la tabla `pending_requests` |

La 12 → 13 recrea `note` en lugar de usar `DROP COLUMN`, que SQLite no admite
con `minSdk 30`.

> La versión 11 se publicó con `fallbackToDestructiveMigration`, así que hay
> instalaciones reales en ese esquema. `MigracionTest` cubre cada salto y la
> cadena completa 11 → 13.

## Por qué desapareció `pending_requests`

La cola guardaba peticiones serializadas para reenviarlas al volver la
conexión. Tenía tres problemas:

1. **Se autodestruía.** `processPendingRequests` borraba cada fila en un
   `finally`, con éxito o sin él. Un fallo de red y la petición se perdía.
2. **Crecía sin techo.** Con el backend caído, `createNote` encolaba en cada
   guardado una petición que no se iba a enviar nunca.
3. **Era una estructura paralela** que había que mantener en sincronía con
   `note`.

Con `synced` en cada nota, reconciliar es `SELECT * FROM note WHERE synced = 0`.
`sincronizarPendientes` sube esas notas y las marca con el id que devuelve el
servidor.

## Regla de escritura

**Room primero e incondicional; la red después.**

```kotlin
suspend fun createNote(context, request): Result<Note> = runCatching {
    val local = buildLocalNote(context, request)
    dao.insertNote(local)              // sin condiciones

    if (BackendGate.isReachable()) {   // efecto secundario que puede fallar
        runCatching { notesService.createNote(request) }
            .onSuccess { dao.insertNote(local.copy(remoteId = it.id, synced = true)) }
    }
    local
}
```

Ningún fallo de red puede propagarse como fallo de la operación. El orden
inverso —red primero, Room después— es lo que hacía que ninguna puntuación se
guardara con el backend caído.

## Contenido didáctico

`assets/procedimientos.json`, leído por `ContenidoRepository` con caché en
memoria.

```json
{
  "version": 1,
  "modulos": {
    "asepsia": [
      { "clave": "stepsLavadoClinico",
        "titulo": "Lavado de manos clínico",
        "pasos": ["Retire las joyas y suba las mangas arriba del codo.", "..."] }
    ]
  }
}
```

17 técnicas, 212 pasos. Antes vivían como constantes en cuatro `Steps.kt`, así
que corregir una errata exigía recompilar y publicar.

**La numeración la genera la interfaz por índice.** En los `Steps.kt` iba
escrita dentro de cada cadena (`"1. "`, `"2. "`), de modo que insertar un paso
obligaba a renumerar los siguientes a mano. Una prueba verifica que ningún paso
traiga numeración escrita.

Las pantallas aportan la imagen y la sinopsis de cada técnica —eso es
presentación—; el título y los pasos vienen del JSON.

### Siguiente paso natural

El día que vuelva el backend, servir este mismo JSON desde el servidor permite
corregir una errata sin publicar. La estructura ya está preparada: `version`
existe para eso.
