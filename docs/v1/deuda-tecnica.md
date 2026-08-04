# Deuda técnica

Hallazgos ordenados por severidad. Cada uno con ubicación exacta.

## Crítico

### 1. Logs de red exponen cuerpos y token en release
`network/ApiClient.kt:47-77`

Hay dos interceptores de log activos sin guarda de `BuildConfig.DEBUG`:

```kotlin
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

Además `Log.d(TAG, "setToken() - Token configurado: $newToken")`
(`ApiClient.kt:27`) y `Log.d(TAG, "authInterceptor - Token actual: $token")`
(`ApiClient.kt:35`) escriben el token en logcat.

En una build de release cualquier app con permiso de lectura de logs, o
cualquiera con el dispositivo en la mano y `adb`, ve credenciales y
cuerpos de petición completos.

**Arreglo:** condicionar el nivel a `BuildConfig.DEBUG` y borrar los
`Log.d` del token.

```kotlin
level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
```

### 2. `bodyInterceptor` consume el cuerpo de la respuesta
`network/ApiClient.kt:52-77`

```kotlin
response.body?.let { responseBody ->
    val responseBodyString = responseBody.string()   // ← consume el stream
    Log.d(TAG, "📦 Response Body: $responseBodyString")

    response.newBuilder()                            // ← se construye…
        .body(okhttp3.ResponseBody.create(responseBody.contentType(), responseBodyString))
        .build()                                     // …y el valor se descarta
} ?: response
```

El `let` devuelve la respuesta reconstruida, pero el interceptor está
declarado como lambda de expresión única: el resultado del `let` **sí**
es el valor de retorno. El problema real es distinto y sigue siendo
grave — `ResponseBody.create(ContentType, String)` está deprecado desde
OkHttp 4 y el patrón es frágil: si en el futuro alguien añade una línea
después del `let`, el cuerpo queda consumido y toda llamada falla con
`IllegalStateException: closed`.

**Arreglo:** eliminar `bodyInterceptor` entero. `HttpLoggingInterceptor`
en nivel `BODY` ya hace exactamente esto, y lo hace bien.

### 3. Release firmada con la clave de debug
`app/build.gradle.kts:31`

```kotlin
release {
    isMinifyEnabled = true
    signingConfig = signingConfigs.getByName("debug")
}
```

Un artefacto firmado con la keystore de debug no es publicable en Play
Store, y si se distribuye fuera de la tienda cualquiera puede firmar una
actualización maliciosa: la clave de debug es pública y compartida.

**Arreglo:** crear un `signingConfig` real con la keystore en
`local.properties` o variables de entorno, nunca en el repositorio.

## Alto

### 4. `fallbackToDestructiveMigration` borra los datos del usuario
`rooms/appDatabase/DatabaseProvider.kt:22`

Con la app en modo offline, Room es la única copia de los resultados de
quizzes. Cada subida de `version` en `AppDatabase` (hoy en 11) los
elimina en silencio.

**Arreglo:** escribir `Migration` explícitas, o como mínimo
`exportSchema = true` más una migración desde la 11 en adelante.

### 5. `ChangePasswordSection` siempre falla
`components/general/ChangePasswordSection.kt` → `ChangePasswordViewModel:34`
→ `UserRepository.changePassword`

Visible en el modal de ajustes de `UserScreen`. Llama a
`POST usuario/change-password` con token vacío: 401 garantizado. El
usuario ve un formulario funcional que nunca puede completarse. Detalle
en [backend.md](backend.md).

### 6. Código muerto: la isla de login
Siete archivos sin referencias entrantes, más `AuthService` y diez DTOs.
Inventario completo en [backend.md](backend.md).

No rompe nada, pero infla el APK, alarga la compilación y —lo más caro—
hace creer que existe un flujo de autenticación que no existe.

## Medio

### 7. `versionName` no era una versión
`app/build.gradle.kts:19`

Histórico: `1.0` → `1.2` → `"Production release 2.0"` →
`"Feat: User profile"` → `"Feat: offline always"`. Ese texto se muestra
al usuario en Play Store y en los ajustes del sistema.

**Resuelto** en la rama `2.1.0` (`versionName = "2.1.0"`,
`versionCode = 10`). Ver [build-release.md](build-release.md).

### 8. Colisión potencial de `User.id`
`repository/AuthRepository.kt:67`

```kotlin
val generatedId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
```

Es la clave primaria de `user`. La colisión es improbable con un usuario
único por dispositivo, pero el valor tampoco es estable: no hay nada que
lo ligue a una identidad. Si algún día se resincroniza con el servidor,
este id no significa nada.

### 9. `CoroutineScope` sin cancelación
`rooms/appDatabase/DatabaseProvider.kt:34`

```kotlin
CoroutineScope(Dispatchers.IO).launch { ... }
```

Scope creado ad hoc, sin `SupervisorJob` ni vínculo a ningún ciclo de
vida. Al ser una operación única de arranque el impacto es mínimo, pero
el patrón se copia con facilidad.

### 10. Contenido didáctico hardcodeado en Kotlin
`ui/study/*/Steps.kt`

Todos los textos de los módulos viven en listas de Kotlin. Corregir una
errata obliga a recompilar y publicar. Nada está en `strings.xml`, así
que además no hay ruta a la localización.

### 11. Doble mecanismo para ocultar la barra inferior
`MainActivity.kt:67` y `ui/navbar/BottomNavBar.kt:32`

Dos listas independientes de rutas excluidas. Añadir una pantalla exige
recordar ambas.

### 12. Rutas por literal en `SplashScreen`
`ui/splash/SplashScreen.kt:48`

```kotlin
navController.navigate(if (isLoggedIn) "main" else "register")
```

Las constantes `Routes.MAIN` y `Routes.REGISTER` existen justo al lado.

## Bajo

### 13. Sin cobertura de tests
`app/src/test/` y `app/src/androidTest/` contienen únicamente los stubs
de la plantilla de Android Studio (`ExampleUnitTest`,
`ExampleInstrumentedTest`). Cero tests reales.

Candidatos de mayor retorno: `ImcViewModel` y `PamViewModel` (cálculo
puro, sin dependencias), y `NoteDao.hasReachedMaxAttempts` (regla de
negocio con consulta SQL no trivial).

### 14. `Note.date` es `String`
`rooms/entitys/Note.kt`

Impide ordenar por fecha de forma fiable y obliga a parsear para
cualquier comparación.

### 15. Historial de git con 126 MB de binarios
`app/release/` quedó sin trackear en el commit `23ebef5`, pero los blobs
antiguos siguen en el historial. Recuperar ese espacio exige
`git filter-repo` y un force-push que rompe los clones existentes.

### 16. `MateriaDao.insertAll` sin `onConflict`
`rooms/dao/MateriaDao.kt:17`

`@Insert` sin estrategia lanza `SQLiteConstraintException` ante un id
duplicado. Hoy lo protege la doble guarda de `loadInitialMaterias`, pero
es un filo innecesario: `OnConflictStrategy.REPLACE` lo vuelve idempotente.

## Orden sugerido

1. Hallazgos 1–3 antes de cualquier publicación. Son de seguridad.
2. Decidir el rumbo del backend (hallazgos 5 y 6) — desbloquea la
   limpieza de ~15 archivos.
3. Hallazgo 4 antes del siguiente cambio de esquema en Room.
4. El resto, oportunista.
