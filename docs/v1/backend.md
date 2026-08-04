# Estado del backend y residuos

Resultado de auditar qué quedó del backend tras el commit `eb3a210`
("modo offline permanente").

## Resumen

La migración a offline **solo desactivó la autenticación**. Notas y
perfil siguen intactos apuntando a la API. El resultado es un sistema en
tres estados:

| Estado | Qué es |
|---|---|
| Muerto | Código sin ninguna referencia entrante. Compila, nunca se ejecuta |
| Vivo y roto | Se ejecuta, llama a la API y falla por token vacío |
| Vivo y correcto | Todo lo local: Room, quizzes, calculadoras |

## Configuración de red

`network/ApiClient.kt`:

```kotlin
private const val BASE_URL  = "https://practica-pro-back.vercel.app/"
private const val LOCAL_URL = "http://192.168.0.7:3000/"
private const val IS_LOCAL  = false
```

`IS_LOCAL` es una constante de compilación, no un `buildConfigField`, así
que cambiar de entorno exige editar el fuente. La IP `192.168.0.7` es una
dirección LAN concreta de una máquina de desarrollo.

`res/xml/network_security_config.xml` permite tráfico en claro hacia
`10.0.2.2` (el host desde el emulador) y `192.168.0.7`. El manifiesto
declara `INTERNET` y `ACCESS_NETWORK_STATE`.

## Vivo y roto: llamadas que aún salen

`AuthRepository.buildLocalUser` guarda `token = ""`. El interceptor de
`ApiClient` filtra por nulidad, no por vacío:

```kotlin
token?.let {
    requestBuilder.addHeader("Authorization", "Bearer $it")
}
```

Cadena vacía no es `null`, así que **cada petición sale con la cabecera
`Authorization: Bearer ` vacía** y el servidor responde 401.

Rutas que siguen activas:

| Origen | Repositorio | Endpoint |
|---|---|---|
| `NotesViewModel:21` `getNotes` | `NotesRepository.getNotes` | `GET notas` |
| `NotesViewModel:35` `createNote` | `NotesRepository.createNote` | `POST notas` |
| `MainScreen:45` | `NotesRepository.processPendingRequests` | `POST notas/offline` |
| `ChangePasswordViewModel:34` | `UserRepository.changePassword` | `POST usuario/change-password` |

Las notas degradan con elegancia: `NotesRepository.getNotes` tiene un
`recoverCatching` que cae a Room, así que el fallo de red es invisible
para el usuario. **`ChangePasswordSection` no degrada**: está visible en
el modal de ajustes de `UserScreen` y siempre fallará.

`UserRepository.getUserProfile()` está definido pero nadie lo llama —
`UserScreen` lee el perfil de Room. Método muerto dentro de un objeto
vivo.

## Muerto: la isla de login

`ui/login/LoginScreen.kt` tiene **cero referencias entrantes**. Al
eliminarse `Routes.LOGIN`, arrastró consigo todo su árbol:

```
LoginScreen  (0 refs)
├── LoginViewModel
│   └── EmailNotConfirmedException
├── VerificationCodeModal
│   └── VerificationViewModel
└── ResetPasswordModal
    └── ResetPasswordViewModel
```

Archivos sin uso:

- `ui/login/LoginScreen.kt`
- `viewmodel/LoginViewModel.kt`
- `viewmodel/VerificationViewModel.kt`
- `viewmodel/ResetPasswordViewModel.kt`
- `components/modals/VerificationCodeModal.kt`
- `components/modals/ResetPasswordModal.kt`
- `repository/exceptions/EmailNotConfirmedException.kt`

## Muerto: `AuthService`

`service/AuthService.kt` tiene **cero referencias**. `AuthRepository`
dejó de crear el servicio; sus cinco endpoints ya no se invocan desde
ninguna parte:

```
POST auth/login
POST auth/register
POST auth/confirm-email
POST auth/resend-verification
POST auth/reset-password
```

Con `AuthService` muerto, los DTOs que solo él usaba quedan huérfanos:

| DTO | Referenciado por |
|---|---|
| `LoginRequest` | solo `AuthService` |
| `RegisterRequest` | solo `AuthService` |
| `RegisterResponse` | solo `AuthService` |
| `AuthResponse` | solo `AuthService` |
| `ConfirmationRequest` | solo `AuthService` |
| `ConfirmationResponse` | solo `AuthService` |
| `EmailRequest` | solo `AuthService` |
| `ResetPasswordRequest` | solo `AuthService` |
| `UserData` | solo `AuthResponse` (cascada) |
| `LoginErrorResponse` | **cero referencias** |

## Muerto: stubs de `AuthRepository`

Cuatro métodos devuelven `Result.failure` de forma incondicional y sus
únicos llamadores están en la isla muerta:

```kotlin
suspend fun login(...)                  = Result.failure(...)  // ← LoginViewModel (muerto)
suspend fun sendResetPasswordCode(...)  = Result.failure(...)  // ← ResetPasswordViewModel (muerto)
suspend fun confirmEmail(...)           = Result.failure(...)  // ← VerificationViewModel (muerto)
suspend fun resendVerificationEmail(...)= Result.failure(...)  // ← VerificationViewModel (muerto)
```

Solo `register` y `logout` están vivos.

## Vivo: lo que sí funciona sin red

- `NetworkObserver` y `ConnectivityIndicator`.
- `AppLifecycleObserver`.
- Todo `rooms/` — quizzes, notas locales, materias, usuario.
- Los cuatro módulos de estudio y las dos calculadoras.
- `NotesRepository.getNotes` gracias a su fallback a Room.

## Dos caminos posibles

**A — Offline de verdad.** Borrar la isla de login, `AuthService` y sus
DTOs; recortar `NotesRepository` y `UserRepository` a Room; eliminar
`ChangePasswordSection` del modal de ajustes; quitar Retrofit, OkHttp y
Gson de `build.gradle.kts` junto con sus reglas de ProGuard; retirar el
permiso `INTERNET` y el `network_security_config`. La app pierde toda
superficie de red.

**B — Restaurar la sincronización.** Devolver un token real a
`buildLocalUser`, o hacer que el interceptor omita la cabecera cuando el
token esté vacío (`token?.takeIf { it.isNotBlank() }?.let { ... }`), y
decidir qué hacer con el registro local frente a las cuentas del
servidor.

La decisión afecta a unos 15 archivos y no debería posponerse: hoy el
código sugiere que la sincronización funciona cuando no lo hace.
