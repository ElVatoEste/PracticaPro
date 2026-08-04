# Módulos y navegación

## Grafo de navegación

Definido en `navigation/AppNavigation.kt`. Destino inicial: `SPLASH`.

| Ruta | Constante | Pantalla |
|---|---|---|
| `splash` | `Routes.SPLASH` | `ui/splash/SplashScreen.kt` |
| `register` | `Routes.REGISTER` | `ui/register/RegisterScreen.kt` |
| `main` | `Routes.MAIN` | `ui/main/MainScreen.kt` |
| `calculadora` | `Routes.CALCULADORA` | `ui/calculadora/CalculadoraScreen.kt` |
| `imc` | `Routes.IMC` | `ui/calculadora/ImcScreen.kt` |
| `pam` | `Routes.PAM` | `ui/calculadora/PamScreen.kt` |
| `tecnicas` | `Routes.TECNICAS` | `ui/study/asepsia/AsepsiaScreen.kt` |
| `quiz_screen` | `Routes.QUIZ_SCREEN` | `ui/study/asepsia/quiz/AsepsiaQuiz.kt` |
| `procedimientos` | `Routes.PROCEDIMIENTOS` | `ui/study/procedimientos/ProcedScreen.kt` |
| `quiz_procedimientos` | `Routes.QUIZ_PROCEDIMIENTOS` | `ui/study/procedimientos/quiz/ProcQuiz.kt` |
| `quiz_proc_tf` | `Routes.QUIZ_PROC_TF` | `.../quiz/TrueFalseQuizScreen.kt` |
| `administracion` | `Routes.ADMINISTRACION` | `ui/study/medicamentos/MedicamentosScreen.kt` |
| `urgencias` | `Routes.URGENCIAS` | `ui/study/urgencias/UrgenciasScreen.kt` |
| `user` | `Routes.USER` | `ui/user/UserScreen.kt` |

No existe ruta `login`. Se eliminó en el commit `eb3a210`.

## Flujo de arranque

```
SPLASH
  └─ lee user de Room
       ├─ user != null && expirationDate > now  →  MAIN
       └─ en caso contrario                     →  REGISTER
                                                     └─ registro local → MAIN
```

`SplashScreen.kt:48` navega con literales (`"main"` / `"register"`), no
con las constantes `Routes.*`. Funciona, pero es frágil ante renombres.

El logout (`ui/navbar/BottomNavBar.kt:122`) borra usuario y notas de Room
y redirige a `REGISTER`.

## Módulos de estudio

Los cuatro siguen la misma estructura: una pantalla con secciones
scrolleables, imágenes, vídeo opcional y diálogos multipaso.

| Módulo | Pantalla | Contenido de pasos | Quiz |
|---|---|---|---|
| Asepsia / Técnicas | `AsepsiaScreen.kt` | `asepsia/Steps.kt` | `AsepsiaQuiz` (opción múltiple) |
| Procedimientos | `ProcedScreen.kt` | `procedimientos/Steps.kt` | `ProcQuiz` + `TrueFalseQuizScreen` |
| Medicamentos / Administración | `MedicamentosScreen.kt` | `medicamentos/Steps.kt` | — |
| Urgencias | `UrgenciasScreen.kt` | `urgencias/Steps.kt` | — |

Cada `Steps.kt` es una lista estática de datos: el contenido didáctico
está hardcodeado en Kotlin, no en `strings.xml` ni en assets. Cambiar un
texto implica recompilar.

### Composables compartidos

| Composable | Archivo | Uso |
|---|---|---|
| `SectionTitle`, `SectionContent` | `components/module/` | Encabezados y cuerpo de sección |
| `TechniqueCard` | `components/module/TechniqueCard.kt` | Tarjeta de técnica con imagen |
| `AnimatedModuleCard`, `ModuleCard` | `components/module/` | Tarjetas del menú principal |
| `MultiStepDialog` | `components/general/MultiStepDialog.kt` | Diálogo paso a paso; recibe `DialogState` |
| `VideoPlayerScreen` | `components/general/VideoPlayerScreen.kt` | Wrapper de ExoPlayer sobre `AndroidView` |
| `ActionButton` | `components/general/ActionButton.kt` | Botón primario |
| `Table` | `components/general/Table.kt` | Tabla estática |

`DialogState` (`viewmodel/helper/DialogState.kt`) agrupa
`showDialog`, `title` y `steps` en un solo objeto, para no dispersar el
estado del diálogo en varios `remember`.

## Quizzes

Dos ViewModels según el formato:

- `QuizViewModel` — opción múltiple, con temporizador
  (`components/quizes/AnimatedTimeBar.kt`) y `Question`
  (`viewmodel/helper/Question.kt`).
- `TrueFalseQuizViewModel` — verdadero/falso.

Componentes de soporte en `components/quizes/`: `ProgressBar`,
`Feedback`, `FinalSummary`, `InstructionsDialog`.

### Puntuación y límite de intentos

Al terminar, el resultado se guarda como `Note` en Room (`score`,
`attempt`, `date`, `subjectId`, `subjectName`).

El acceso al quiz se bloquea vía `NoteDao.hasReachedMaxAttempts(subjectId)`.
Cada pantalla de módulo consulta ese valor en un `LaunchedEffect` y
deshabilita el botón. Ejemplo en `AsepsiaScreen.kt:50`:

```kotlin
LaunchedEffect(key1 = navController.currentBackStackEntry) {
    val noteDao = DatabaseProvider.getDatabase(context).noteDao()
    isButtonEnabled = !noteDao.hasReachedMaxAttempts(1)
}
```

El `subjectId` va hardcodeado en cada pantalla (1 = TECNICAS, etc.),
alineado con la tabla `materia` que siembra `DatabaseProvider`.

## Calculadoras

- **IMC** — `ImcScreen` + `ImcViewModel`. Usa `GenderToggleButton`.
- **PAM** — presión arterial media. `PamScreen` + `PamViewModel`.

Cálculo puro en memoria, sin persistencia ni red.

## Perfil

`ui/user/UserScreen.kt` carga nombre y correo desde Room
(`userViewModel.loadUserProfileFromRoom`). Incluye `SettingsModalContent`
con `ChangePasswordSection`, que **sí llama a la API** — ver
[backend.md](backend.md).
