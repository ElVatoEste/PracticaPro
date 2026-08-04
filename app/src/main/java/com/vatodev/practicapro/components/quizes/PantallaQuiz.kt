package com.vatodev.practicapro.components.quizes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado

/** Estado visual de una opción de respuesta. */
enum class EstadoOpcion { NEUTRA, ELEGIDA, CORRECTA, INCORRECTA }

/**
 * Andamiaje común de las evaluaciones: cuenta de preguntas, tiempo restante,
 * enunciado y opciones.
 */
@Composable
fun PantallaQuiz(
    preguntaActual: Int,
    totalPreguntas: Int,
    tiempoRestante: Float,
    tiempoMaximo: Float,
    enunciado: String,
    onCerrar: () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val estado = LocalEstado.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Salir",
                tint = estado.textoSuave,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onCerrar)
            )
            Text(
                text = "PREGUNTA ${preguntaActual + 1} / $totalPreguntas",
                style = EtiquetaTracked.copy(fontSize = 13.sp),
                color = estado.textoSuave
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    tint = estado.logro,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = "%.0f s".format(tiempoRestante),
                    style = Dato.copy(fontSize = 14.sp),
                    color = estado.logro
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        ProgressBar(currentStep = preguntaActual, totalSteps = totalPreguntas)
        Spacer(Modifier.height(8.dp))
        AnimatedTimeBar(timeLeft = tiempoRestante, maxTime = tiempoMaximo)

        Spacer(Modifier.height(36.dp))
        Text(
            text = enunciado,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp, lineHeight = 33.sp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(28.dp))
        contenido()
        Spacer(Modifier.height(28.dp))
    }
}

/** Opción de respuesta. El borde y el icono portan el estado, no un relleno saturado. */
@Composable
fun OpcionQuiz(
    letra: String,
    texto: String,
    estadoOpcion: EstadoOpcion,
    habilitada: Boolean,
    onClick: () -> Unit
) {
    val estado = LocalEstado.current
    val acento = when (estadoOpcion) {
        EstadoOpcion.CORRECTA -> estado.progreso
        EstadoOpcion.INCORRECTA -> estado.error
        EstadoOpcion.ELEGIDA -> estado.logro
        EstadoOpcion.NEUTRA -> estado.filete
    }
    val fondo = when (estadoOpcion) {
        EstadoOpcion.NEUTRA -> MaterialTheme.colorScheme.surface
        else -> acento.copy(alpha = 0.12f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(fondo)
            .border(if (estadoOpcion == EstadoOpcion.NEUTRA) 1.dp else 2.dp, acento, RectangleShape)
            .clickable(enabled = habilitada, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = letra,
            style = Dato.copy(fontSize = 13.sp),
            color = if (estadoOpcion == EstadoOpcion.NEUTRA) estado.textoSuave else acento
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        when (estadoOpcion) {
            EstadoOpcion.CORRECTA -> Icon(Icons.Default.Check, null, tint = acento, modifier = Modifier.size(19.dp))
            EstadoOpcion.INCORRECTA -> Icon(Icons.Default.Close, null, tint = acento, modifier = Modifier.size(19.dp))
            else -> Unit
        }
    }
}
