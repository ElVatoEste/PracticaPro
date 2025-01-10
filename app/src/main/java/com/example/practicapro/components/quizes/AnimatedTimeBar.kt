package com.example.practicapro.components.quizes

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedTimeBar(timeLeft: Float, maxTime: Float) {
    val animatedTime = remember { Animatable(timeLeft) }
    LaunchedEffect(timeLeft) {
        animatedTime.animateTo(timeLeft)
    }
    LinearProgressIndicator(
        progress = animatedTime.value / maxTime,
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp),
        color = if (timeLeft > maxTime * 0.3f) Color(0xFF7DBB00) else Color(0xFFFF5252)
    )
}