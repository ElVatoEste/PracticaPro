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

## Resumen del plan

| Fase | Objetivo | Bloquea a |
|---|---|---|
| **F1** | Detener la pérdida de datos | todo |
| **F2** | Interruptor único de backend | F3, F4 |
| **F3** | Migraciones de Room reales | F4 |
| **F4** | Esquema de sincronización (`synced`, `remoteId`) | — |
| **F5** | Seguridad: logs, firma, token | publicación |
| **F6** | Toolchain, SDK y dependencias | — |

F1 es urgente y no depende de nada. F6 se puede adelantar o retrasar sin
afectar al resto, pero conviene hacerlo antes de F4 para no migrar el
esquema dos veces.
