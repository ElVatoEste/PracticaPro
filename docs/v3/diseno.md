# Sistema de diseño

## Paleta

Muestreada de `res/drawable/logo_final.jpg` con un contador de píxeles, no
elegida a ojo.

| Rol | Claro | Oscuro | Significado |
|---|---|---|---|
| `progreso` | `#4E7500` | `#7DBB00` | Avance, acierto, acción principal |
| `logro` | `#674FA3` | `#9B7EE0` | Puntuación, racha, estado intermedio |
| `error` | `#E5624A` | `#E5624A` | Fallo, valor fuera de rango |
| fondo | `#FBFAFD` | `#121019` | — |
| elevado | `#F1EFF6` | `#1C1827` | Bloques de cifras, tarjetas |
| filete | `#DDD8E8` | `#2E2840` | Separadores de 1 px |

El verde de marca `#7DBB00` no contrasta lo suficiente sobre fondo claro, así
que el tema claro usa `#4E7500` para texto. El fondo oscuro `#121019` es un
negro con matiz morado, derivado de la marca en lugar de neutro.

**Los tres colores de estado no son intercambiables.** No viven en el
`ColorScheme` de Material sino en `ColoresDeEstado`, expuesto por
`LocalEstado`, precisamente para que no se usen como `primary`/`secondary`
decorativos.

`dynamicColor` está desactivado: la identidad no puede depender del fondo de
pantalla del usuario, y un color de estado debe significar lo mismo en todos
los dispositivos.

## Tipografía

Tres roles, tres familias, empaquetadas en `res/font`. No son *downloadable
fonts*: la app es offline-first y esas exigen red en el primer uso.

| Rol | Familia | Uso |
|---|---|---|
| Etiqueta | Barlow Condensed | Epígrafes en mayúsculas con tracking |
| Texto | IBM Plex Sans | Contenido didáctico y enunciados |
| Cifra | IBM Plex Mono | Puntuaciones, IMC, PAM, temporizadores |

El monoespaciado no es estético: las cifras necesitan ancho de dígito constante
para que columnas sucesivas queden alineadas y para que un contador no baile al
cambiar de valor.

Estilos expuestos: `EtiquetaTracked`, `Lectura` (44 sp para lecturas grandes) y
`Dato` (cifras en línea).

## El elemento recurrente: `Escala`

Sitúa un valor entre tramos con significado clínico. Aparece en tres sitios:

- **IMC**: bandas por sexo y edad, con el peso ideal derivado de la banda normal
- **PAM**: incluye el umbral de hipoperfusión en 60 mmHg
- **Resultado de evaluación**: la puntuación se lee situada, no como cifra suelta

```kotlin
Escala(
    valor = 23.5f,
    unidad = "kg/m²",
    tramos = listOf(Tramo("Peso normal", 25.0f, estado.progreso), ...),
    minimo = 14f,
    maximo = 45f
)
```

Los tramos que consume la interfaz **son los mismos** que usa el clasificador
del ViewModel. Si divergieran, la escala mentiría; una prueba lo comprueba.

La aguja se posiciona con un `Layout` propio en vez de pesos en un `Row`:
con fracción 0 o 1 el hueco colapsa y la marca se desplaza.

## Andamiajes

En lugar de repetir estructura en cada pantalla:

- **`PantallaModulo`** — imagen a sangre con degradado hasta el fondo, cuerpo
  desplazable y llamada a la evaluación con intentos consumidos. La acción es
  opcional: Medicamentos y Urgencias no tienen evaluación.
- **`PantallaQuiz`** — cabecera, cuenta de preguntas, tiempo y enunciado.
- **`SeccionModulo`** y **`FilaTecnica`** — secciones y filas regladas.

## Decisiones de forma

**Filetes en lugar de tarjetas.** Un `Filete` de 1 px separa sin encerrar. Las
tarjetas elevadas se retiraron de listas de módulos, técnicas e historial:
apilar tarjetas dentro de tarjetas era el patrón más repetido del código
anterior.

**Rectangular, sin elevación.** `BotonPrimario` y `BotonSecundario` usan
`RectangleShape` y `elevation = null`. La jerarquía la marcan el contraste y la
posición.

**El color porta el estado, el texto explica.** `OpcionQuiz` y `Feedback` no
repiten en mayúsculas lo que el color ya dice.

## Progreso por segmentos

`ProgressBar` dibuja un segmento por pregunta en lugar de una barra continua.
Una barra dice qué fracción llevas; los segmentos dicen además cuántas quedan.
El mismo componente numera los pasos en `MultiStepDialog`.
