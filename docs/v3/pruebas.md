# Pruebas

37 unitarias de JVM y 7 instrumentadas. Antes había dos stubs de la plantilla
de Android Studio.

```bash
./gradlew :app:testDebugUnitTest            # 37, sin dispositivo
./gradlew :app:connectedDebugAndroidTest    # 7, requiere dispositivo
```

## Qué cubre

### `ImcViewModelTest` — 10

Fórmula, umbrales por sexo y edad, peso ideal, y entradas inválidas (talla
cero, peso negativo).

La prueba que más importa: **la clasificación coincide siempre con la banda que
contiene al valor**. La escala de la interfaz consume esa misma lista de
bandas, así que si el clasificador y las bandas divergieran, la escala mentiría
sin que nada fallara.

### `PamViewModelTest` — 8

Fórmula `(2·diastólica + sistólica) / 3`, las cinco bandas, el umbral de
hipoperfusión en 60 mmHg y el rechazo de diastólica ≥ sistólica.

### `CatalogoTest` — 6

`MODULOS` es la fuente única de módulos, rutas y `subjectId`; antes estaba
duplicado entre `MainScreen`, las pantallas de módulo y los quizzes. Verifica
que no haya duplicados y que `MATERIAS` cubra todos los módulos.

### `ContenidoTest` — 7

El JSON de contenido no lo valida el compilador. Comprueba estructura, que
ninguna técnica quede sin pasos ni título, que no haya numeración escrita a
mano y que el recuento migrado siga siendo 17 técnicas y 212 pasos.

### `OpcionQuizTest` — 6

Estados de las opciones de respuesta. Antes de este cambio, al responder se
atenuaban todas y **nunca se marcaba cuál era la correcta**.

### `MigracionTest` — 7, instrumentada

Cada salto de esquema y la cadena 11 → 13, con datos representativos: notas del
servidor y locales, fechas numéricas y de texto, usuario y cola de peticiones.

Necesita dispositivo porque `MigrationTestHelper` abre bases SQLite reales.

## Qué NO cubre

**Nada de interfaz.** No hay pruebas de Compose. Un `Escala` que se pinte mal,
un `PantallaModulo` con el layout roto o un contraste insuficiente en modo
oscuro pasarían sin que nada fallara.

**Nada de navegación.** Que `Routes.SIN_BARRA` esconda la barra donde debe, o
que el splash elija destino correctamente, no está verificado.

**`QuizViewModel` y `TrueFalseQuizViewModel`.** Contienen el temporizador y el
cálculo de puntuación con bonificación por tiempo, que es lógica con reglas.
Son los siguientes candidatos.

**`NotesRepository`.** La regla "Room primero, red después" es el arreglo más
importante del proyecto y solo está verificada por lectura. Probarla exige un
doble de `NotesService` y una base en memoria.

## Cómo leer un fallo

Las pruebas usan nombres en español con backticks, así que el informe se lee
como frases:

```
la clasificacion coincide siempre con la banda que contiene al valor FAILED
    expected:<Sobrepeso> but was:<Peso normal>
```

El informe HTML queda en `app/build/reports/tests/testDebugUnitTest/index.html`.
