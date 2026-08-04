package com.vatodev.practicapro.components.general

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vatodev.practicapro.components.quizes.ProgressBar
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado

/**
 * Pasos de una técnica, uno por pantalla.
 *
 * La numeración se genera por índice: el contenido de `assets` guarda solo el
 * texto, así que insertar un paso no obliga a renumerar los siguientes.
 */
@Composable
fun MultiStepDialog(title: String, steps: List<String>, onDismiss: () -> Unit) {
    if (steps.isEmpty()) return

    var pasoActual by remember { mutableIntStateOf(0) }
    val estado = LocalEstado.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RectangleShape,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = title.uppercase(),
                    style = EtiquetaTracked.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(14.dp))
                ProgressBar(currentStep = pasoActual, totalSteps = steps.size)

                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = "%02d".format(pasoActual + 1),
                        style = Dato.copy(fontSize = 13.sp),
                        color = estado.progreso,
                        modifier = Modifier.padding(end = 14.dp)
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 110.dp, max = 340.dp)
                            .verticalScroll(rememberScrollState())
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        AnimatedContent(
                            targetState = pasoActual,
                            transitionSpec = {
                                fadeIn(tween(220)) togetherWith fadeOut(tween(220))
                            },
                            label = "paso"
                        ) { paso ->
                            Text(
                                text = steps[paso],
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (pasoActual > 0) {
                        TextButton(
                            onClick = { pasoActual-- },
                            colors = ButtonDefaults.textButtonColors(contentColor = estado.textoSuave)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Anterior")
                            Spacer(Modifier.padding(horizontal = 3.dp))
                            Text("ANTERIOR", style = EtiquetaTracked.copy(fontSize = 12.sp))
                        }
                    } else {
                        Spacer(Modifier.padding(8.dp))
                    }

                    if (pasoActual < steps.lastIndex) {
                        TextButton(
                            onClick = { pasoActual++ },
                            colors = ButtonDefaults.textButtonColors(contentColor = estado.progreso)
                        ) {
                            Text("SIGUIENTE", style = EtiquetaTracked.copy(fontSize = 12.sp))
                            Spacer(Modifier.padding(horizontal = 3.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Siguiente")
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(contentColor = estado.progreso)
                        ) {
                            Text("CERRAR", style = EtiquetaTracked.copy(fontSize = 12.sp))
                        }
                    }
                }
            }
        }
    }
}
