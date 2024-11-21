package com.example.practicapro.ui.screen.medicamentos

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.roundToInt

@Composable
fun MinijuegoMedicamentosScreen(navController: NavController) {
    val steps = listOf(
        "1. Verifica el nombre del paciente y la receta.",
        "2. Revisa el medicamento y la dosis.",
        "3. Prepara el equipo necesario.",
        "4. Administra el medicamento según la vía indicada.",
        "5. Monitorea al paciente después de la administración."
    )

    var draggableSteps by remember { mutableStateOf(steps.shuffled()) }
    val placeholders = remember { mutableStateListOf<String?>(null, null, null, null, null) }
    var draggingItem by remember { mutableStateOf<String?>(null) }
    var feedbackVisible by remember { mutableStateOf(false) }
    var correctSteps by remember { mutableIntStateOf(0) }
    var modalVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (modalVisible) {
            CaseModal(onDismiss = { modalVisible = false })
        } else {
            Text(
                text = "Caso Clínico: Administración de Medicamentos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF7DBB00)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Arrastra los pasos hacia los espacios correspondientes:",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            placeholders.forEachIndexed { index, placeholder ->
                Placeholder(
                    text = placeholder,
                    isHighlighted = draggingItem != null,
                    draggingItem = draggingItem, // Pasar draggingItem
                    onDrop = { droppedStep ->
                        if (placeholders[index] == null) { // Aceptar solo si el espacio está vacío
                            placeholders[index] = droppedStep
                            draggableSteps = draggableSteps.filter { it != droppedStep }
                            draggingItem = null
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pasos disponibles:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                textAlign = TextAlign.Start
            )

            draggableSteps.forEach { step ->
                DraggableStep(
                    text = step,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    onDragStart = { draggingItem = step },
                    onDragEnd = { draggingItem = null }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    correctSteps = placeholders.zip(steps).count { it.first == it.second }
                    feedbackVisible = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DBB00)),
                enabled = placeholders.none { it == null }
            ) {
                Text("Verificar Orden", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (feedbackVisible) {
                FeedbackDialog(correctSteps, steps.size) {
                    feedbackVisible = false
                    if (correctSteps == steps.size) {
                        navController.navigate("medicamentos")
                    }
                }
            }
        }
    }
}

@Composable
fun Placeholder(
    text: String?,
    isHighlighted: Boolean,
    draggingItem: String?, // Agregado como parámetro
    onDrop: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = if (isHighlighted && text == null) Color(0xFFD3E2FF) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        change.consume()
                    },
                    onDragEnd = {
                        if (text == null && draggingItem != null) {
                            onDrop(draggingItem)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text ?: "Arrastra aquí",
            fontSize = 16.sp,
            color = if (text != null) Color.Black else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DraggableStep(
    text: String,
    modifier: Modifier = Modifier,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .background(
                if (isDragging) Color(0xFFD3E2FF) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        onDragStart()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragEnd()
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        onDragEnd()
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FeedbackDialog(correctSteps: Int, totalSteps: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Resultados del Intento") },
        text = {
            Text(
                text = "Pasos correctos: $correctSteps de $totalSteps.\n" +
                        if (correctSteps == totalSteps) {
                            "¡Excelente! Has completado la actividad correctamente."
                        } else {
                            "Algunos pasos están incorrectos. Revisa y vuelve a intentarlo."
                        },
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}

@Composable
fun CaseModal(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Caso Clínico") },
        text = {
            Text(
                "El paciente presenta síntomas que requieren un medicamento por vía intravenosa. Tu tarea es ordenar los pasos para administrarlo correctamente."
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}
