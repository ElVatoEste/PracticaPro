# Arquitectura

## Capas

```
UI (Compose)          ui/*, components/*
      ↓ observa State / llama funciones
ViewModel             viewmodel/*
      ↓ suspend fun, devuelve Result<T>
Repository            repository/*
      ↓                    ↓
Room (local)          Retrofit (remoto)
rooms/*               service/* + network/ApiClient
```

MVVM sin inyección de dependencias. Los repositorios son `object`
(singletons de Kotlin) y los servicios Retrofit se crean con `by lazy`
dentro de cada uno. No hay Hilt, Koin ni contenedor DI.

## Paquetes

| Paquete | Rol |
|---|---|
| `ui/` | Una carpeta por pantalla. `ui/study/*` agrupa los módulos de estudio |
| `components/` | Composables reutilizables: `general/`, `modals/`, `module/`, `quizes/` |
| `viewmodel/` | Un ViewModel por pantalla o feature. `viewmodel/helper/` para tipos de estado |
| `repository/` | Orquesta Room + red. Devuelve `Result<T>` |
| `service/` | Interfaces Retrofit puras (solo anotaciones) |
| `model/` | DTOs de la API (Gson) |
| `rooms/` | `appDatabase/`, `dao/`, `entitys/` |
| `network/` | `ApiClient`, `NetworkObserver`, `AppLifecycleObserver`, `ConnectivityIndicator` |
| `utils/` | `NoteMapper` (DTO ↔ entidad Room), `TimeUtils` (extensiones `.days()`, `.minutes()`) |
| `entitys/` | `ApiNote` — DTO de nota; nota: separado de `rooms/entitys/` pese al nombre |

## Convenciones observadas

- Estado de UI en `mutableStateOf` expuesto como `State<T>` desde el
  ViewModel, no `StateFlow`. Excepción: `NetworkObserver` usa
  `MutableStateFlow`.
- Los repositorios devuelven `Result<T>` construido con `runCatching` /
  `recoverCatching`. Los errores se propagan como `Exception` con
  mensaje en español, ya listo para mostrar en un snackbar.
- Todo acceso a Room va envuelto en `withContext(Dispatchers.IO)`.
- Comentarios y mensajes de error en español; nombres de símbolos
  mezclan español e inglés (`Materia`, `NoteDao`, `subjectName`).

## Punto de entrada

`MainActivity.onCreate` (`MainActivity.kt:38`) hace, en orden:

1. Registra `AppLifecycleObserver` en `ProcessLifecycleOwner`.
2. `WindowCompat.setDecorFitsSystemWindows(window, false)` — dibujo
   edge-to-edge.
3. `NetworkObserver.startObserving(this)`.
4. `DatabaseProvider.loadInitialMaterias(this)` — siembra las 5 materias.
5. `userViewModel.loadTokenFromRoom(...)` — restaura el token en
   `ApiClient`.
6. `setContent { Scaffold { ... AppNavigation(navController) } }`.

El `Scaffold` oculta la `BottomNavigationBar` en las rutas `SPLASH` y
`REGISTER` (`MainActivity.kt:67`). `BottomNavBar` mantiene además su
propio `excludedRoutes` con las pantallas de quiz
(`ui/navbar/BottomNavBar.kt:32`) — dos mecanismos independientes para
lo mismo.

## Observador de red

`NetworkObserver` (`network/NetworkObserver.kt`) expone
`isNetworkAvailable: StateFlow<Boolean>` desde un
`ConnectivityManager.NetworkCallback`. Lo consumen:

- `ConnectivityIndicator` — snackbar de "sin conexión".
- `MainScreen` — dispara `NotesRepository.processPendingRequests`.
- `NotesRepository` y `UserRepository` — guardan antes de cada llamada
  de red.

## Cola de peticiones offline

`rooms/entitys/PendingRequest` guarda `endpoint`, `method`, `payload`
(JSON serializado con kotlinx.serialization), `userId` y `timestamp`.
`NotesRepository.processPendingRequests(context)` la vacía cuando vuelve
la conexión; se invoca desde `ui/main/MainScreen.kt:45`.

El diseño es correcto, pero hoy no llega a completarse: las peticiones
salen sin token válido. Ver [backend.md](backend.md).
