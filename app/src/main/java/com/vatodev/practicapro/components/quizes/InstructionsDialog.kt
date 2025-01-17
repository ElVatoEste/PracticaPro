package com.vatodev.practicapro.components.quizes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun InstructionsDialog(onStartClick: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onStartClick()
            }) {
                Text("Iniciar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = {
            Text("Instrucciones")
        },
        text = {
            Text(
                """
                Lee cada pregunta cuidadosamente.
                Selecciona una respuesta para obtener retroalimentación al instante.
                ¡Buena suerte!
                """.trimIndent()
            )
        }
    )
}
