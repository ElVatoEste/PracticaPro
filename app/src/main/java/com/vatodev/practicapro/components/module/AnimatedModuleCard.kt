package com.vatodev.practicapro.components.module

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedModuleCard(
    module: Module,
    modifier: Modifier = Modifier,
    delayMillis: Int,
    onClick: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(50f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 250)
        )
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 250)
        )
    }

    // Cancelar animación al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            scope.launch {
                alpha.stop()
                offsetY.stop()
            }
        }
    }

    ModuleCard(
        module = module,
        modifier = modifier
            .graphicsLayer(
                translationY = offsetY.value,
                alpha = alpha.value
            ),
        onClick = onClick
    )
}
