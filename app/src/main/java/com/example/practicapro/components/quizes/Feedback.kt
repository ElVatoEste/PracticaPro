package com.example.practicapro.components.quizes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
fun Feedback(
    isCorrect: Boolean?,
    explanation: String,
    timeBonus: Int,
    onNext: () -> Unit
) {
    if (isCorrect == null) return // No mostrar feedback si no se ha seleccionado una respuesta

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onNext() }, // Avanza solo cuando se hace clic
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) Color(0xFF7DBB00) else Color(0xFFFF5252)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de estado
            Icon(
                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de feedback
            Text(
                text = if (isCorrect) {
                    "¡Correcto! +$timeBonus puntos obtenidos"
                } else {
                    "Incorrecto. Intenta de nuevo."
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Explicación
            Text(
                text = explanation,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
