package com.example.practicapro.ui.screen.medicamentos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import kotlin.math.roundToInt

@Composable
fun MinijuegoMedicamentosScreen(navController: NavController) {
    // Lista de pasos correctos
    val steps = listOf(
        "1. Verifica el nombre del paciente y la receta.",
        "2. Revisa el medicamento y la dosis.",
        "3. Prepara el equipo necesario.",
        "4. Administra el medicamento según la vía indicada.",
        "5. Monitorea al paciente después de la administración."
    )

    // Estado para guardar el orden actual de los pasos (inicialmente desordenados)
    var shuffledSteps by remember { mutableStateOf(steps.shuffled()) }
    var feedbackMessage by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Ordena los Pasos Correctamente",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF7DBB00)
        )

        // Subtítulo
        Text(
            text = "Arrastra los pasos para colocarlos en el orden correcto.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Drag-and-Drop: Pasos interactivos
        shuffledSteps.forEachIndexed { index, step ->
            DraggableStep(
                text = step,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onReorder = { fromIndex, toIndex ->
                    shuffledSteps = shuffledSteps.toMutableList().apply {
                        val movedStep = removeAt(fromIndex)
                        add(toIndex, movedStep)
                    }
                },
                currentIndex = index
            )
        }

        // Botón para verificar el orden
        Button(
            onClick = {
                if (shuffledSteps == steps) {
                    feedbackMessage = "¡Correcto! Has ordenado los pasos correctamente."
                    success = true
                } else {
                    feedbackMessage = "Algunos pasos están en el orden incorrecto. Intenta de nuevo."
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DBB00))
        ) {
            Text("Verificar Orden", color = Color.White, fontWeight = FontWeight.Bold)
        }

        // Feedback
        if (feedbackMessage.isNotEmpty()) {
            Text(
                text = feedbackMessage,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (success) Color(0xFF7DBB00) else Color(0xFFFF5252),
                textAlign = TextAlign.Center
            )

            if (success) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("medicamentos") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DBB00))
                ) {
                    Text("Regresar al Módulo", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DraggableStep(
    text: String,
    modifier: Modifier = Modifier,
    onReorder: (fromIndex: Int, toIndex: Int) -> Unit,
    currentIndex: Int
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(16.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                        // Detect and call onReorder if necessary (can add more logic)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )
    }
}
