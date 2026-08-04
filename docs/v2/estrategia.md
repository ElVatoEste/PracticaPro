# Estrategia: offline-first con backend latente

## La restricción

- El backend está caído. Sin fecha de vuelta.
- No se descarta que vuelva.
- La app debe funcionar al 100 % sin él, hoy.

Eso descarta las dos soluciones fáciles. **Borrar la capa de red** deja
la app limpia pero obliga a reescribir la sincronización desde cero
cuando el servidor vuelva. **Dejarlo como está** mantiene el código
listo, pero la app pierde datos ahora mismo.

La salida es intermedia: aislar, no amputar.

## Principio 1 — Room es la verdad, la red es un extra

Hoy el flujo de escritura es *red primero, Room después*:

```
usuario → API → (si responde) → Room
```

Por eso H1 pierde datos: si la API no responde, no hay Room. Se invierte:

```
usuario → Room → (si se puede) → API
```

La escritura local no depende de nada y no puede fallar por causas
externas. La sincronización pasa a ser un efecto secundario que puede
fracasar sin que el usuario lo note ni pierda nada.

Regla concreta para cada método de repositorio:

1. Escribe en Room. Sin condiciones, sin comprobar red.
2. Devuelve éxito ya, con el dato local.
3. Intenta sincronizar. Si falla, encola. Si encolar falla, registra y
   sigue.

Ningún fallo de red puede propagarse como fallo de la operación.

## Principio 2 — Un solo interruptor

Hoy la condición de red está repartida en cinco sitios
(`NotesRepository` ×3, `UserRepository` ×2), cada uno con su
`NetworkObserver.isNetworkAvailable.first()`. Añadir el estado
"backend caído" a mano en cada uno garantiza olvidos.

Se centraliza:

```kotlin
// network/BackendGate.kt
object BackendGate {
    /** Interruptor de compilación. false mientras el servidor esté caído. */
    val isEnabled: Boolean get() = BuildConfig.BACKEND_ENABLED

    /** El backend está activado y además hay conectividad. */
    suspend fun isReachable(): Boolean =
        isEnabled && NetworkObserver.isNetworkAvailable.first()
}
```

```kotlin
// app/build.gradle.kts, defaultConfig
buildConfigField("boolean", "BACKEND_ENABLED", "false")
```

Toda condición de red del proyecto pasa a ser `BackendGate.isReachable()`.
Reactivar el backend es cambiar `false` por `true` y verificar.

Se puede afinar por tipo de build cuando haga falta probar contra un
servidor local:

```kotlin
buildTypes {
    debug   { buildConfigField("boolean", "BACKEND_ENABLED", "false") }
    release { buildConfigField("boolean", "BACKEND_ENABLED", "false") }
}
```

## Principio 3 — El código muerto se congela, no se borra

La isla de login (7 archivos), `AuthService` y sus 10 DTOs quedan donde
están. Borrarlos ahorra unos pocos KB y cuesta reescribirlos íntegros
cuando el backend vuelva.

En su lugar se marcan. Un encabezado en cada archivo:

```kotlin
// LATENTE: inactivo mientras BackendGate.isEnabled == false.
// Reactivación: docs/v2/plan.md, fase R.
```

Y la ruta de login se registra condicionalmente, en lugar de estar
eliminada:

```kotlin
// navigation/AppNavigation.kt
if (BackendGate.isEnabled) {
    composable(Routes.LOGIN) { LoginScreen(...) }
}
```

Así el flujo de autenticación reaparece con el mismo interruptor, en vez
de exigir un `git revert` sobre un commit de hace meses.

Excepción: `LoginErrorResponse` no tiene ninguna referencia, ni siquiera
desde la isla muerta. Ese sí se borra.

## Principio 4 — La UI no ofrece lo que no puede cumplir

`ChangePasswordSection` está visible en los ajustes de `UserScreen` y
siempre falla. Se oculta tras el mismo interruptor:

```kotlin
if (BackendGate.isEnabled) {
    ChangePasswordSection()
}
```

Mismo criterio para cualquier control cuya única acción sea una llamada
de red.

## Qué pasa con la cola de sincronización

Con el backend caído sin fecha, `pending_requests` plantea un dilema: si
se sigue encolando, la tabla crece sin techo durante meses; si no se
encola, se pierde la posibilidad de reconciliar cuando el servidor
vuelva.

La resolución llega con el esquema de la fase F4: en lugar de una cola
paralela, cada `Note` lleva su propio estado de sincronización.

```kotlin
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: Int? = null,      // id del servidor; null = solo local
    val synced: Boolean = false,    // ya confirmada por el servidor
    val score: Int,
    val attempt: Int,
    val date: Long,                 // epoch millis, no String
    val subjectId: Int,
    val subjectName: String
)
```

Ventajas sobre la cola:

- No hay estructura paralela que mantener en sincronía con `note`.
- No crece sin límite: son las mismas filas que el usuario ya generó.
- La reconciliación futura es una consulta:
  `SELECT * FROM note WHERE synced = 0`.
- `pending_requests` queda obsoleta y se puede retirar.

Mientras F4 no llegue, F1 usa un apaño acotado: **ids locales negativos**.
Los ids del servidor son positivos, así que un id negativo identifica sin
ambigüedad una nota creada en local y nunca colisiona. La migración
posterior los convierte en filas con `synced = false`.

## Lo que no cambia

- La arquitectura MVVM y la estructura de paquetes.
- Room como motor de persistencia.
- Retrofit, OkHttp y los servicios: se quedan, inactivos.
- Los cuatro módulos de estudio y las dos calculadoras: ya son 100 %
  locales y correctos.

## Criterio de éxito

Con el avión en modo vuelo y desinstalando cualquier caché:

1. Registro local → entra a `main`.
2. Completar un quiz → la puntuación aparece en el historial.
3. Cerrar la app por completo y reabrir → la puntuación sigue ahí.
4. Repetir el quiz hasta el máximo → el botón se deshabilita.
5. Ningún mensaje de error de red en toda la sesión.

Hoy fallan los puntos 2, 3 y 4.
