# Pendiente

## Bloqueante para publicar

### La app no se ha ejecutado nunca

Todo está verificado por compilación y pruebas de JVM. Sin abrir la app en un
dispositivo no se sabe si el modo oscuro tiene contraste suficiente, si los
layouts del rediseño se comportan en pantallas reales, ni si la migración
sobrevive sobre una instalación existente.

Los criterios de aceptación de [`../v2/plan.md`](../v2/plan.md) siguen sin
comprobarse. El más importante: completar un quiz en modo avión, matar la app y
verificar que la puntuación sigue en el historial.

```bash
./gradlew :app:installDebug
./gradlew :app:connectedDebugAndroidTest
```

### `keystore.properties` sin rellenar

La keystore está en `C:\Users\bigma\keystore\practicapro.jks`. Falta copiar
`keystore.properties.example` a `keystore.properties` y poner alias y
contraseñas.

**Sin esos cuatro valores la release sale sin firmar.** No hay respaldo a la
keystore de debug: su clave es pública, y firmar con ella permitiría a
cualquiera publicar una actualización suplantando la app.

## Decisiones de producto

### Qué pasa con el registro local cuando vuelva el backend

Hoy `AuthRepository.register` crea un usuario solo en Room, con token vacío.
Cuando el servidor vuelva hay que decidir si esas cuentas migran, si el login
pasa a ser obligatorio, o si conviven.

Conviene resolverlo **antes** de que el backend vuelva, no después. Es el punto
3 de la fase R en [`../v2/plan.md`](../v2/plan.md).

### La tabla de clasificación del IMC

El rediseño sustituyó el botón "Mostrar tabla de clasificación" por la
`Escala`, que muestra los mismos tramos con el valor situado encima y
permanentemente visible. Si se prefiere conservar además la tabla numérica
exacta, hay que reincorporarla.

## Deuda técnica abierta

| Origen | Pendiente |
|---|---|
| v1 #15 | 126 MB de binarios en el historial de git. Recuperarlos exige `git filter-repo` y un force-push que rompe los clones existentes |
| — | Sin pruebas de interfaz ni de navegación (ver [pruebas.md](pruebas.md)) |
| — | `QuizViewModel` y `NotesRepository` sin cobertura |

Cerradas desde v1: logs con token, `bodyInterceptor`, firma en código,
`fallbackToDestructiveMigration`, `ChangePasswordSection`, `versionName`,
rutas literales, `MateriaDao.insertAll`, `User.id`, `CoroutineScope` suelto,
contenido hardcodeado, doble lista de rutas, ausencia de pruebas, `note.date`.

## Código latente

Ocho archivos del flujo de autenticación, marcados con una cabecera `LATENTE`:

```
ui/login/LoginScreen.kt              viewmodel/LoginViewModel.kt
viewmodel/VerificationViewModel.kt   viewmodel/ResetPasswordViewModel.kt
components/modals/VerificationCodeModal.kt
components/modals/ResetPasswordModal.kt
repository/exceptions/EmailNotConfirmedException.kt
service/AuthService.kt
```

Se conservan a propósito: el backend puede volver y reescribirlos costaría más
que mantenerlos. Son los únicos archivos que aún tienen colores fijos.

Reactivar es poner `BACKEND_ENABLED = true` en `build.gradle.kts`.

## Diseño

Tres pantallas diseñadas en Pencil —detalle de módulo, quiz y calculadora— que
no se pudieron exportar: el renderizador devolvía imágenes vacías para nodos
recién creados. Existen en el `.pen` y se ven abriéndolo.

El código de esas tres pantallas sí está implementado; lo que falta es la
referencia visual para contrastar.
