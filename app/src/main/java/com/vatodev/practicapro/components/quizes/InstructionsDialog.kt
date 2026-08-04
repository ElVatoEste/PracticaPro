package com.vatodev.practicapro.components.quizes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado

@Composable
fun InstructionsDialog(onStartClick: () -> Unit, onDismiss: () -> Unit) {
    val estado = LocalEstado.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RectangleShape,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "ANTES DE EMPEZAR",
                style = EtiquetaTracked.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Cada pregunta tiene tiempo limitado. Responder rápido suma puntos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = estado.textoSuave
                )
                Text(
                    text = "La respuesta no se puede cambiar una vez elegida, y el intento cuenta aunque salgas a mitad.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = estado.textoSuave
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onStartClick) {
                Text(
                    text = "EMPEZAR",
                    style = EtiquetaTracked.copy(fontSize = 14.sp),
                    color = estado.progreso
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "CANCELAR",
                    style = EtiquetaTracked.copy(fontSize = 14.sp),
                    color = estado.textoSuave
                )
            }
        }
    )
}
