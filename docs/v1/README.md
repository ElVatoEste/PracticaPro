# PracticaPro — Documentación v1

> **Desactualizado en lo que toca a la interfaz.** El rediseño y el saneamiento posteriores están en [`../v3/`](../v3/README.md).


Aplicación Android nativa de estudio para enfermería: módulos teóricos,
quizzes con puntuación persistida y calculadoras clínicas (IMC, PAM).

Esta documentación describe el estado del proyecto en la rama `2.1.0`
(`versionCode` 10), tras la migración a modo offline.

## Índice

| Documento | Contenido |
|---|---|
| [arquitectura.md](arquitectura.md) | Capas, flujo de datos, convenciones de paquetes |
| [modulos.md](modulos.md) | Pantallas, rutas de navegación y features |
| [persistencia.md](persistencia.md) | Room: entidades, DAOs, ciclo de vida de la BD |
| [backend.md](backend.md) | Estado real de la integración con la API y qué quedó colgando |
| [deuda-tecnica.md](deuda-tecnica.md) | Hallazgos priorizados con ubicación exacta |
| [build-release.md](build-release.md) | Versionado, firma, ProGuard, artefactos |

## Stack

| Componente | Versión |
|---|---|
| Kotlin | 2.0.0 |
| AGP | 8.7.3 |
| Compose BOM | 2024.12.01 |
| Room | 2.6.1 (kapt) |
| Retrofit + Gson | 2.9.0 |
| OkHttp logging-interceptor | 4.11.0 |
| Navigation Compose | 2.8.5 |
| Media3 ExoPlayer | 1.5.1 |
| kotlinx.serialization | 1.6.3 |

`minSdk` 30 · `targetSdk` / `compileSdk` 35 · `applicationId`
`com.vatodev.practicapro` · Java/JVM target 11.

## Estado en una línea

La app funciona sin backend para todo lo que es estudio (módulos,
quizzes, calculadoras, historial de notas local). La autenticación se
desactivó por completo, pero **notas y perfil siguen llamando a la API**
con un token vacío — ver [backend.md](backend.md).

## Arranque rápido

```bash
./gradlew :app:assembleDebug        # compilar
./gradlew :app:installDebug         # instalar en dispositivo/emulador
./gradlew :app:compileDebugKotlin   # solo verificar que compila
```

No hace falta configurar nada: no hay claves ni `local.properties`
requerido más allá del `sdk.dir` que genera Android Studio.

## v2 — offline-first con backend latente

El backend está caído sin fecha de vuelta. La estrategia v2 está en
[../v2/README.md](../v2/README.md): aislar la capa de red tras un
interruptor en lugar de borrarla, y arreglar la pérdida de datos que
documenta [../v2/hallazgos.md](../v2/hallazgos.md).
