package com.example.practicapro.components.quizes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FinalSummary(score: Int, onDismiss: () -> Unit) {
    val message = when {
        score == 100 -> "¡Excelente trabajo! Dominaste este tema."
        score >= 75 -> "Buen trabajo, pero puedes mejorar."
        score >= 50 -> "Buen esfuerzo, sigue practicando para mejorar."
        else -> "Necesitas más práctica para dominar este tema."
    }

    val icon = when {
        score == 100 -> Icons.Default.EmojiEvents // 🏆
        score >= 75 -> Icons.Default.Celebration  // 🎉
        score >= 50 -> Icons.Default.WarningAmber // ⚠️
        else -> Icons.Default.SentimentDissatisfied // 😞
    }


    val backgroundColor = when {
        score == 100 -> Color(0xFF4CAF50) // Verde éxito
        score >= 75 -> Color(0xFFFFC107) // Amarillo dorado
        score >= 50 -> Color(0xFFFF5722) // Naranja oscuro
        else -> Color(0xFFF44336) // Rojo error
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¡Quiz Finalizado!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Puntaje Final",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$score/100",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = message,
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}
