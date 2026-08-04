# PracticaPro — v3: rediseño y saneamiento

Estado del proyecto tras ejecutar el plan v2 completo, rediseñar la interfaz
sobre la paleta de marca y cerrar la mayor parte de la deuda técnica.

Para el estado anterior: [`../v1/`](../v1/README.md) describe la app antes de
todo esto, y [`../v2/`](../v2/README.md) la estrategia offline-first. **Ambos
están desactualizados en lo que toca a la interfaz**; este documento manda.

## Índice

| Documento | Contenido |
|---|---|
| [diseno.md](diseno.md) | Paleta, tipografía y sistema de componentes |
| [datos.md](datos.md) | Esquema v13, migraciones y contenido en assets |
| [pruebas.md](pruebas.md) | Qué cubre la suite y qué no |
| [pendiente.md](pendiente.md) | Lo que queda, con su motivo |

## Lo que cambió

**El tema nunca se aplicaba.** `PracticaproTheme` existía pero `MainActivity`
montaba el `Scaffold` sin envolverlo, así que las pantallas se pintaban con los
colores por defecto de Material más verde a mano en 21 archivos. Conectarlo fue
el primer paso del rediseño.

**La paleta salió del logotipo**, muestreada de `logo_final.jpg`: verde
`#7DBB00` y morado `#674FA3`. El verde que estaba hardcodeado era el de marca;
el morado de la plantilla de Android coincidía con el de marca por casualidad.

**El contenido didáctico dejó de ser código.** Las 17 técnicas y sus 212 pasos
vivían en cuatro `Steps.kt`; ahora están en `assets/procedimientos.json`.

**El esquema llegó a la v13**: `note` lleva su propio estado de sincronización
y la cola `pending_requests` desapareció.

**37 pruebas unitarias y 7 instrumentadas** donde antes había dos stubs.

## Estructura actual

```
components/
  general/   Escala, Etiqueta, Filete, BotonPrimario, BotonSecundario,
             FilaDato, Resumen, Intentos, CampoNumerico, MultiStepDialog,
             NoteCard, VideoPlayerScreen, GenderToggleButton, ActionButton
  module/    PantallaModulo, SeccionModulo, FilaTecnica
  modals/    SettingsModalContent  (+ 2 latentes)
  quizes/    PantallaQuiz, OpcionQuiz, ProgressBar, AnimatedTimeBar,
             Feedback, FinalSummary, InstructionsDialog
model/       Modulo (catálogo MODULOS y MATERIAS) + DTOs
repository/  Auth, Notes, User, Contenido
rooms/       AppDatabase v13, Migrations, 3 entidades, 3 DAO
network/     ApiClient, BackendGate, NetworkObserver, ConnectivityIndicator
```

## Verificación

```bash
./gradlew :app:testDebugUnitTest     # 37 pruebas de JVM
./gradlew :app:connectedDebugAndroidTest   # migraciones, requiere dispositivo
./gradlew :app:assembleRelease       # incluye R8
```

**Nada se ha ejecutado en un dispositivo.** Ver [pendiente.md](pendiente.md).
