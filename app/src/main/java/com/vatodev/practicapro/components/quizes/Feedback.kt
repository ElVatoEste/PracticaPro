package com.vatodev.practicapro.components.quizes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado

/**
 * Resultado de la respuesta. El color del bloque porta el estado; el texto
 * explica en lugar de repetirlo.
 */
@Composable
fun Feedback(
    isCorrect: Boolean?,
    explanation: String,
    timeBonus: Int,
    onNext: () -> Unit
) {
    if (isCorrect == null) return

    val estado = LocalEstado.current
    val acento = if (isCorrect) estado.progreso else estado.error

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(acento.copy(alpha = 0.14f))
            .clickable(onClick = onNext)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = acento,
                modifier = Modifier.size(17.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = if (isCorrect) "CORRECTO" else "INCORRECTO",
                style = EtiquetaTracked.copy(fontSize = 13.sp),
                color = acento
            )
            if (isCorrect && timeBonus > 0) {
                Spacer(Modifier.weight(1f))
                Text(text = "+$timeBonus", style = Dato.copy(fontSize = 14.sp), color = acento)
            }
        }

        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Toca para continuar",
            style = Dato.copy(fontSize = 11.sp),
            color = estado.textoSuave
        )
    }
}
