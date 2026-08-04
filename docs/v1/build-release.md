# Build y release

## Configuración

`app/build.gradle.kts`:

| Campo | Valor |
|---|---|
| `applicationId` | `com.vatodev.practicapro` |
| `namespace` | `com.vatodev.practicapro` |
| `minSdk` | 30 (Android 11) |
| `targetSdk` / `compileSdk` | 35 |
| `versionCode` | 10 |
| `versionName` | `2.1.0` |
| Java / JVM target | 11 |

`buildConfigField("String", "DEVELOPER_NAME", "\"Vato_dev\"")` con
`buildConfig = true`.

## Versionado

Desde la rama `2.1.0` se retoma el versionado semántico, abandonado tras
el `versionCode` 3.

| `versionCode` | `versionName` |
|---|---|
| 1 | `1.0` |
| 3 | `1.2` |
| 5 | `Production release 2.0` |
| 6 | *(sin cambio)* |
| 7 | `Feat: User profile` |
| 9 | `Feat: offline always` |
| **10** | **`2.1.0`** |

Los `versionName` intermedios eran mensajes de commit, no versiones. La
base 2.1.0 parte del 2.0 del `versionCode` 5.

**Reglas a partir de aquí:**

- El nombre de la rama es la versión: `2.1.0`, `2.2.0`, …
- Un feature suma `0.1.0`; un arreglo suma `0.0.1`.
- `versionCode` se incrementa en 1 por cada release publicada,
  independientemente del semver. Play Store exige que sea estrictamente
  creciente y nunca se reutiliza.

## Firma

```kotlin
release {
    isMinifyEnabled = true
    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    signingConfig = signingConfigs.getByName("debug")   // ← pendiente
}
```

> La build de release usa la keystore de **debug**. No es publicable en
> Play Store. Ver hallazgo 3 en [deuda-tecnica.md](deuda-tecnica.md).

Al configurar la keystore real, mantenerla fuera del repositorio:
`local.properties` (ya ignorado) o variables de entorno en CI.

## ProGuard / R8

`isMinifyEnabled = true` en release, con `proguard-android-optimize.txt`
más `app/proguard-rules.pro`. Reglas presentes:

```proguard
-keep @retrofit2.http.* class * { *; }
-keep class retrofit2.** { *; }
-keep interface retrofit2.http.* { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.annotations.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
```

Si se toma el camino "offline de verdad" descrito en
[backend.md](backend.md), estas reglas se van junto con las dependencias.

## Artefactos

`app/release/` **no se versiona**. Se sacó del control de versiones en el
commit `23ebef5`: contenía 126 MB de binarios generados (`.aab`, `.apk`,
baseline profiles, `output-metadata.json`).

El `.gitignore` cubría `/release` en la raíz pero no `/app/release`, que
es donde el plugin de Android escribe. Ahora están ambas reglas.

Los blobs ya presentes en el historial de git no desaparecen con esto;
eliminarlos requiere reescribir el historial.

## Comandos

```bash
./gradlew :app:compileDebugKotlin   # verificación rápida de compilación
./gradlew :app:assembleDebug        # APK de debug
./gradlew :app:installDebug         # instalar en dispositivo conectado
./gradlew :app:bundleRelease        # AAB de release (firma de debug hoy)
./gradlew :app:lint                 # lint de Android
```

## Dependencias

Declaradas en el catálogo de versiones `gradle/libs.versions.toml`. Room
usa `kapt`, no KSP — migrar a KSP acortaría los tiempos de compilación,
ya que kapt obliga a generar stubs de Java.
