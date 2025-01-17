package com.vatodev.practicapro.components.quizes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(currentStep: Int, totalSteps: Int) {
    LinearProgressIndicator(
        progress = currentStep.toFloat() / totalSteps,
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        color = MaterialTheme.colorScheme.primary
    )
}