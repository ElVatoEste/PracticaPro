package com.vatodev.practicapro.components.quizes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vatodev.practicapro.ui.theme.LocalEstado

/**
 * Tiempo restante. Pasa a rojo bajo el 30 %: el cambio de color es el aviso,
 * sin necesidad de un contador aparte.
 */
@Composable
fun AnimatedTimeBar(timeLeft: Float, maxTime: Float, modifier: Modifier = Modifier) {
    val estado = LocalEstado.current
    val fraccion by animateFloatAsState(
        targetValue = (timeLeft / maxTime).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 250),
        label = "tiempo"
    )

    Box(
        modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(estado.filete)
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraccion)
                .fillMaxHeight()
                .background(if (fraccion > 0.3f) estado.progreso else estado.error)
        )
    }
}
