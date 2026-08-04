package com.vatodev.practicapro.components.general

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.Lectura
import com.vatodev.practicapro.ui.theme.LocalEstado
import kotlin.math.max
import kotlin.math.min

/**
 * Un tramo de la escala. [hasta] es el límite superior en unidades reales; el
 * último tramo lo ignora y absorbe el resto.
 */
data class Tramo(
    val etiqueta: String,
    val hasta: Float,
    val color: Color
)

/**
 * Escala de medición clínica: sitúa un valor entre tramos con significado.
 *
 * Es el elemento recurrente de la interfaz. Se usa en las calculadoras para
 * ubicar IMC y PAM, y con [Tramo] de un solo color para progreso de quiz e
 * intentos consumidos.
 */
@Composable
fun Escala(
    valor: Float,
    unidad: String,
    tramos: List<Tramo>,
    minimo: Float,
    maximo: Float,
    modifier: Modifier = Modifier
) {
    require(tramos.isNotEmpty()) { "La escala necesita al menos un tramo" }

    val estado = LocalEstado.current
    val activo = tramos.firstOrNull { valor < it.hasta } ?: tramos.last()
    val fraccion = ((valor - minimo) / (maximo - minimo)).coerceIn(0f, 1f)
    val fraccionAnimada by animateFloatAsState(
        targetValue = fraccion,
        animationSpec = tween(durationMillis = 450),
        label = "aguja"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = formatearValor(valor),
                style = Lectura,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = unidad,
                style = Dato.copy(fontSize = 13.sp),
                color = estado.textoSuave,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = activo.etiqueta.uppercase(),
                style = EtiquetaTracked.copy(fontSize = 15.sp),
                color = activo.color,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            tramos.forEachIndexed { indice, tramo ->
                val anterior = if (indice == 0) minimo else tramos[indice - 1].hasta
                val fin = if (indice == tramos.lastIndex) maximo else tramo.hasta
                val peso = max(0.01f, (fin - anterior) / (maximo - minimo))
                Box(
                    Modifier
                        .weight(peso)
                        .height(10.dp)
                        .background(if (tramo == activo) tramo.color else tramo.color.copy(alpha = 0.28f))
                )
            }
        }

        Aguja(
            fraccion = fraccionAnimada,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tramos.dropLast(1).forEach {
                Text(
                    text = formatearValor(it.hasta),
                    style = Dato.copy(fontSize = 11.sp),
                    color = estado.textoSuave
                )
            }
        }
    }
}

/**
 * Marca vertical situada por fracción del ancho. Un Row con pesos no sirve:
 * con fracción 0 o 1 el hueco colapsa y la marca se desplaza.
 */
@Composable
private fun Aguja(fraccion: Float, color: Color, modifier: Modifier = Modifier) {
    Layout(
        content = {
            Box(
                Modifier
                    .width(2.dp)
                    .height(14.dp)
                    .background(color)
            )
        },
        modifier = modifier.height(14.dp)
    ) { medibles, restricciones ->
        val marca = medibles.first().measure(restricciones.copy(minWidth = 0))
        layout(restricciones.maxWidth, marca.height) {
            val x = ((restricciones.maxWidth - marca.width) * fraccion).toInt()
            marca.place(min(max(0, x), restricciones.maxWidth - marca.width), 0)
        }
    }
}

private fun formatearValor(v: Float): String =
    if (v == v.toInt().toFloat()) v.toInt().toString() else String.format("%.1f", v)
