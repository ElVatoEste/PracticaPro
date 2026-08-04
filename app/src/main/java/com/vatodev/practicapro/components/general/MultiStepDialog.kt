package com.vatodev.practicapro.components.general

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MultiStepDialog(title: String, steps: List<String>, onDismiss: () -> Unit) {
    var currentStep by remember { mutableIntStateOf(0) } // Controla el paso actual

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize(), // Ajusta el tamaño al contenido
            shape = RoundedCornerShape(24.dp), // Bordes redondeados
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Fondo del diálogo
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize() // Ajusta la columna al contenido
            ) {
                // Título del Diálogo
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7DBB00)// Color primario
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Indicador de Progreso
                LinearProgressIndicator(
                    progress = { (currentStep + 1) / steps.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color(0xFF7DBB00),
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) // Color del fondo de la barra
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contenido Animado del Paso Actual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 400.dp) // Restricción mínima y máxima
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.background) // Fondo del contenido
                        .padding(8.dp)
                ) {
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with
                                    fadeOut(animationSpec = tween(300))
                        },
                        label = "Step Transition"
                    ) { step ->
                        Text(
                            text = steps[step],
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground // Color del texto
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botones de Navegación
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (currentStep > 0) {
                        TextButton(
                            onClick = { currentStep-- },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF7DBB00)) // Color del botón
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Anterior"
                            )
                            Text("Anterior")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (currentStep < steps.size - 1) {
                        TextButton(
                            onClick = { currentStep++ },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF7DBB00)) // Color del botón
                        ) {
                            Text("Siguiente")
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Siguiente"
                            )
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF7DBB00)) // Color del botón
                        ) {
                            Text("Cerrar")
                        }
                    }
                }
            }
        }
    }
}



