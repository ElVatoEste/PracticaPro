# F6 — Toolchain, SDK y dependencias

## Estado actual

| Componente | Versión | Fecha aproximada |
|---|---|---|
| Gradle wrapper | 8.9 | jul 2024 |
| AGP | 8.7.3 | nov 2024 |
| Kotlin | 2.0.0 | may 2024 |
| `compileSdk` / `targetSdk` | 35 | Android 15 |
| `minSdk` | 30 | Android 11 |
| JVM target | 11 | — |
| Compose BOM | 2024.12.01 | dic 2024 |
| Room | 2.6.1 (**kapt**) | — |
| Navigation Compose | 2.8.5 | — |
| Retrofit / converter-gson | 2.9.0 | 2020 |
| okhttp logging-interceptor | 4.11.0 | 2023 |
| Media3 | 1.5.1 | — |
| kotlinx.serialization | 1.6.3 | — |

El proyecto lleva parado desde finales de 2024.

> **Los números de versión de destino que siguen deben verificarse antes
> de aplicarlos** — cambian cada pocas semanas. Lo que no cambia es el
> orden de los pasos y los cambios de ruptura, que es lo que este
> documento fija. Para consultar lo vigente:
> `./gradlew dependencyUpdates`, el AGP Upgrade Assistant de Android
> Studio, o [developer.android.com/jetpack/androidx/versions](https://developer.android.com/jetpack/androidx/versions).

---

## El JDK: inventario y elección

**No hay bloqueo.** Gradle arranca correctamente:

```
$ ./gradlew --version
Gradle 8.9
Launcher JVM:  17.0.12 (Oracle Corporation 17.0.12+8-LTS-286)
Daemon JVM:    C:\Program Files\Java\jdk-17 (no JDK specified, using current Java home)
```

`JAVA_HOME` apunta a `C:\Program Files\Java\jdk-17`, y Gradle usa
`JAVA_HOME`, no el `java` del `PATH`.

### El Java 8 del PATH es inofensivo (para Gradle)

```
$ where java
C:\Program Files (x86)\Common Files\Oracle\Java\java8path\java.exe   → 1.8.0_491 (JRE)

$ where javac
C:\Program Files\Java\jdk-17\bin\javac.exe                           → 17.0.12
```

El instalador de Oracle coloca un shim de Java 8 delante en el `PATH`.
Solo afecta a invocaciones directas de `java`; Gradle, Maven y Android
Studio resuelven por `JAVA_HOME` o por su propio JBR. Conviene limpiarlo
para evitar sorpresas al ejecutar un `.jar` a mano, pero no bloquea nada
de este plan.

### JDKs instalados

| Ruta | Tipo | Versión |
|---|---|---|
| `C:\Program Files\Java\jdk-17` | JDK | **17.0.12 LTS** ← `JAVA_HOME` |
| `C:\Program Files\Java\jdk-21` | JDK | 21.0.6 LTS |
| `C:\Program Files\Java\jdk-23` | JDK | 23.0.1 |
| `C:\Program Files\Java\jre1.8.0_491` | JRE | 1.8.0_491 ← shim del `PATH` |
| `C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot` | JDK | 21.0.6 LTS |
| `C:\Users\bigma\.jdks\jbr-17.0.12` | JDK | 17.0.12 |
| `C:\Users\bigma\.jdks\jbr-17.0.14` | JDK | 17.0.14 |
| `C:\Program Files\Android\Android Studio\jbr` | JDK | **21.0.10** (ene 2026) |
| `C:\Program Files\JetBrains\IntelliJ IDEA 2024.3.1\jbr` | JDK | 21.0.5 |

Nueve instalaciones. Sobra material; falta acuerdo entre ellas.

### El problema real: IDE y terminal no coinciden

| Quién compila | Con qué JDK |
|---|---|
| Terminal (`./gradlew`) | 17.0.12 — por `JAVA_HOME` |
| Android Studio | 21.0.10 — su JBR, según `.idea/misc.xml` (`jbr-21`) |

Dos JDKs distintos sobre el mismo proyecto. Funciona por ahora, pero es
justo el escenario donde aparece un fallo que solo se reproduce en un
sitio, y donde la caché de Gradle se invalida sin motivo aparente al
alternar entre IDE y terminal.

### Recomendación: unificar en JDK 21

JDK 21 es LTS, es lo que ya usa Android Studio, y es el destino natural
para las versiones recientes de AGP. Fijarlo por proyecto para que no
dependa de variables de entorno:

```properties
# gradle.properties
org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr
```

Alternativa sin atarse a la ruta de instalación de Android Studio, que
cambia al actualizar el IDE:

```kotlin
// app/build.gradle.kts
kotlin {
    jvmToolchain(21)
}
```

El *toolchain* es la opción más robusta: Gradle localiza o descarga el
JDK adecuado y el build deja de depender de qué tenga configurado cada
máquina. Es también lo que hace reproducible el build en CI.

Verificación tras el cambio:

```bash
./gradlew --version    # Daemon JVM debe decir 21
```

> No confundir el JDK que **ejecuta** Gradle (21) con el `jvmTarget` del
> bytecode que **produce** (hoy 11, paso 5 lo sube a 17). Son ajustes
> independientes: un JDK 21 puede emitir bytecode 17 sin problema.

---

## Orden obligado

Cada paso deja el proyecto compilando. No agrupar: si algo rompe, hay que
saber qué lo rompió.

```
1. Unificar JDK (IDE y terminal en 21)
2. Gradle wrapper
3. AGP
4. Kotlin
5. JVM target 11 → 17
6. kapt → KSP
7. compileSdk / targetSdk 36
8. Compose BOM + AndroidX
9. Red: Retrofit / OkHttp
10. Gson → kotlinx.serialization
```

---

## 1–3 · Gradle y AGP

AGP y Gradle van emparejados: cada AGP exige un rango de Gradle. Subir
primero el wrapper, luego AGP.

```bash
./gradlew wrapper --gradle-version 8.13   # verificar la vigente
```

Luego `agp` en `gradle/libs.versions.toml`.

**Rupturas conocidas al pasar de AGP 8.7:**

- `buildFeatures` está declarado **dos veces** en
  `app/build.gradle.kts` (líneas 35 y 48). Hoy funciona; conviene
  unificarlo en un solo bloque antes de subir.
- `kotlinOptions { }` está deprecado en favor de
  `kotlin { compilerOptions { } }`.
- `freeCompilerArgs += listOf("-Xincremental")` — `-Xincremental` ya no
  existe en Kotlin 2.x. La compilación incremental está activa por
  defecto. **Quitar esa línea.**
- AGP 9 vuelve obligatorio el namespace y elimina restos de Groovy DSL;
  si se salta a la 9, revisar las notas de migración completas.

## 4 · Kotlin

Kotlin, el plugin de Compose y kotlinx-serialization comparten ciclo. En
el catálogo actual:

```toml
kotlin = "2.0.0"
kotlinx-serialization = { id = "...", version = "2.0.0" }   # ← literal duplicado
```

El plugin de serialización repite el número en vez de usar
`version.ref = "kotlin"`. Al subir Kotlin es fácil olvidarse de uno.
**Unificarlo:**

```toml
kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

## 5 · JVM target 11 → 17

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_17 } }
```

Sin efecto sobre `minSdk`: el desugaring de D8 se encarga. Requerido por
las versiones recientes de AGP.

## 6 · kapt → KSP

El cambio con mayor retorno del lote. kapt genera stubs de Java para
todo el módulo antes de procesar anotaciones; KSP lee Kotlin
directamente. En un proyecto con un solo procesador (Room) el ahorro
típico está entre el 30 % y el 50 % del tiempo de build limpio.

```kotlin
// plugins
id("com.google.devtools.ksp") version "<emparejada con Kotlin>"
// dependencies
ksp(libs.androidx.room.compiler)   // en lugar de kapt(...)
```

Quitar `id("org.jetbrains.kotlin.kapt")`. La versión de KSP va atada a la
de Kotlin (`2.0.21-1.0.28`, por ejemplo) — no se eligen por separado.

Room admite KSP desde la 2.5. Sin cambios en el código: las mismas
anotaciones, otro procesador.

> Hacer este paso **antes de F4**. Migrar el esquema con kapt y luego
> cambiar a KSP obliga a regenerar y revalidar el esquema exportado.

## 7 · compileSdk / targetSdk 36

**Verificar la fecha límite vigente de Play Store.** La política exige
que las actualizaciones apunten a un nivel de API dentro del año
siguiente al último Android mayor. Con Android 16 (API 36) publicado,
`targetSdk 35` deja de aceptarse para actualizaciones en algún momento
de 2026 — comprobar en la Play Console, que avisa con el plazo exacto de
esta app.

`compileSdk` puede subir solo, sin `targetSdk`, y es lo primero:

```kotlin
compileSdk = 36
targetSdk  = 36    // tras revisar los cambios de comportamiento
```

Con `minSdk 30` la mayoría de cambios de comportamiento de API 33–36 ya
aplican. Puntos a revisar para esta app en concreto:

- **Edge-to-edge obligatorio** en API 35+. Ya se hace
  (`WindowCompat.setDecorFitsSystemWindows(window, false)` +
  `windowInsetsPadding`), así que el riesgo es bajo — pero conviene
  revisar cada pantalla en un dispositivo con barra de gestos.
- **Foreground service types** — no aplica, la app no tiene servicios.
- **`POST_NOTIFICATIONS`** — no aplica, no hay notificaciones.
- **Restricciones de red en texto claro** — `network_security_config.xml`
  permite `10.0.2.2` y `192.168.0.7`. Con `BACKEND_ENABLED = false` ese
  fichero puede retirarse; si vuelve el backend, apunta solo a HTTPS.

## 8 · Compose BOM y AndroidX

Subir el BOM arrastra todo Compose de una vez. Después, `material3` y
`material-icons-*` deben quedar **sin versión explícita** para que el BOM
mande. Hoy los iconos van sueltos:

```toml
compose-material-icons = "1.7.6"   # ← fuera del BOM
```

Eso puede desalinear los iconos del resto de Compose. Al subir, quitar
esa versión y dejar que el BOM la resuelva.

Aparte del BOM: `navigation-compose`, `lifecycle-*`, `activity-compose`,
`core-ktx` y `media3`.

Nota sobre `material-icons-extended`: pesa varios MB y trae miles de
vectores. Si solo se usan unos pocos, conviene comprobar qué aporta
frente a `material-icons-core` — R8 elimina los no usados en release,
pero la build de debug los arrastra enteros.

## 9 · Retrofit y OkHttp

Retrofit 2.9.0 es de 2020 y arrastra OkHttp 3.x por transitividad,
mientras el proyecto declara `logging-interceptor` 4.11.0 por su cuenta:
**dos versiones de OkHttp en el árbol de dependencias**. Verificable con:

```bash
./gradlew :app:dependencies --configuration releaseRuntimeClasspath
```

Al subir Retrofit (2.11+ o la serie 3.x), fijar OkHttp explícitamente vía
su BOM para que ambas coincidan.

Con `BACKEND_ENABLED = false` esta actualización no es urgente — nada de
esto se ejecuta. Pero tampoco es gratis dejarlo: son dependencias que
entran igual en el APK.

## 10 · Gson → kotlinx.serialization

El proyecto usa **los dos serializadores a la vez**:

| Gson | kotlinx.serialization |
|---|---|
| `entitys/ApiNote.kt` | `model/CreateNoteRequest.kt` |
| `model/ConfirmationRequest.kt` | `model/CreateOfflineNoteRequest.kt` |
| `model/ConfirmationResponse.kt` | `repository/NotesRepository.kt` |
| `model/LoginErrorResponse.kt` | |
| `model/PasswordRequest.kt` | |
| `repository/UserRepository.kt` | |

Dos librerías para el mismo trabajo: más APK, dos juegos de reglas de
ProGuard, dos modelos mentales.

Consolidar en kotlinx.serialization — ya está en el proyecto, es la
opción idiomática en Kotlin, no usa reflexión (mejor con R8) y elimina
las reglas `-keep class com.google.gson.**` de
`proguard-rules.pro`.

```kotlin
// converter
implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:<ver>")
// y en ApiClient
.addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
```

Requiere anotar los DTOs restantes con `@Serializable`. Como la mayoría
son de la isla latente, el trabajo real se reduce a `ApiNote`,
`PasswordRequest` y `UserRepository`.

**Este paso puede posponerse sin coste** mientras el backend esté caído.

---

## Verificación por paso

```bash
./gradlew :app:compileDebugKotlin    # el más rápido; detecta la mayoría de rupturas
./gradlew :app:assembleDebug         # incluye procesamiento de recursos y anotaciones
./gradlew :app:lint                  # avisos de API nueva y deprecaciones
./gradlew :app:assembleRelease       # única forma de detectar problemas de R8
```

`assembleRelease` es el que más falla tras subir dependencias: reglas de
ProGuard que ya no valen. No dejarlo para el final del lote.

## Prioridad realista

| Paso | Urgencia | Motivo |
|---|---|---|
| Unificar JDK en 21 | Media | Hoy IDE (21) y terminal (17) difieren |
| targetSdk 36 | **Alta** | Plazo de Play Store — verificar en Play Console |
| kapt → KSP | Media | Ahorro de tiempo en cada build; antes de F4 |
| Gradle / AGP / Kotlin | Media | Requisito de los dos anteriores |
| Compose BOM | Media | Correcciones y rendimiento |
| Retrofit / OkHttp | Baja | Código inactivo hoy |
| Gson → kotlinx | Baja | Limpieza, sin urgencia |
