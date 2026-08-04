# PracticaPro — v2: offline-first con backend latente

## Contexto

El backend (`practica-pro-back.vercel.app`) **está caído** y no hay fecha
de reactivación. Tampoco se descarta que vuelva.

Eso fija la estrategia: la app debe funcionar **completamente offline
hoy**, sin amputar la capa de red, para poder reactivarla el día que el
servidor vuelva sin rehacer el trabajo.

## Estado actual, sin adornos

La v1 documentó que el paso a offline solo desactivó el login. La
auditoría para la v2 encontró algo peor:

> **Los resultados de los quizzes no se guardan. Nunca. Hoy, con el
> backend caído, cada puntuación que el usuario obtiene se pierde.**

No es una regresión del modo offline: el bug es anterior y el apagón del
backend lo volvió permanente. Detalle y demostración en
[hallazgos.md](hallazgos.md).

## Índice

| Documento | Contenido |
|---|---|
| [hallazgos.md](hallazgos.md) | Los tres bugs de pérdida de datos, con la traza exacta |
| [estrategia.md](estrategia.md) | Offline-first con backend latente: el diseño |
| [plan.md](plan.md) | Fases F1–F6, criterios de aceptación, orden obligado |
| [actualizaciones.md](actualizaciones.md) | Toolchain, SDK y dependencias |

Para el estado previo al v2, ver [`../v1/`](../v1/README.md).

## Los dos principios

**1. Room es la verdad. La red es un extra.**
Toda escritura va a Room primero y sin condiciones. La sincronización es
un efecto secundario opcional que puede fallar sin consecuencias.

**2. El backend se apaga con un interruptor, no con un borrado.**
Un único `BuildConfig.BACKEND_ENABLED` gobierna toda la superficie de
red. Hoy en `false`. El día que el servidor vuelva, se pone en `true` y
se verifica — no se reescribe.

## Estado de ejecución

Las seis fases están aplicadas en la rama `feat/plan-v2`. F6 se ejecutó
primero para no migrar el esquema de Room dos veces.

| Fase | Objetivo | Estado |
|---|---|---|
| **F6** | Toolchain, SDK y dependencias | ✅ |
| **F1** | Detener la pérdida de datos | ✅ |
| **F2** | Interruptor único de backend | ✅ |
| **F3** | Migraciones de Room reales | ✅ |
| **F4** | Esquema de sincronización (`synced`, `remoteId`) | ✅ |
| **F5** | Seguridad: logs, firma, token | ✅ código; falta rellenar `keystore.properties` |

Verificado con `clean` + `:app:assembleDebug` + `:app:assembleRelease`
(R8 incluido). **No se ha ejecutado la app en un dispositivo**: la
verificación es de compilación, no de comportamiento. Los criterios de
aceptación de [plan.md](plan.md) siguen pendientes de comprobar en
runtime, en particular la migración 11 → 12 sobre una instalación real.

### Pendiente

- Rellenar `keystore.properties` a partir de
  `keystore.properties.example`. Sin los cuatro valores la release sale
  **sin firmar**.
- Ejecutar los criterios de aceptación en dispositivo.
- Retirar la columna heredada `note.date` y la tabla `pending_requests`
  en la versión 13 del esquema.
- Decidir el punto 3 de la fase R: qué pasa con el registro local frente
  a las cuentas del servidor cuando el backend vuelva.
