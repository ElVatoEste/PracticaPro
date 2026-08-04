package com.vatodev.practicapro.components.quizes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vatodev.practicapro.ui.theme.LocalEstado

/**
 * Progreso por segmentos, uno por pregunta. Una barra continua solo indica qué
 * fracción llevas; los segmentos dicen además cuántas preguntas quedan.
 */
@Composable
fun ProgressBar(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    val estado = LocalEstado.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        repeat(totalSteps.coerceAtLeast(1)) { indice ->
            val color = when {
                indice < currentStep -> estado.progreso
                indice == currentStep -> estado.logro
                else -> estado.filete
            }
            Box(
                Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(color)
            )
        }
    }
}
