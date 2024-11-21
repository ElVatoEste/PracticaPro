package com.example.practicapro.ui.screen.asepsia

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizScreen(onDismiss: () -> Unit) {
    var currentQuestion by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    val questions = listOf(
        "¿Cuál es el propósito principal del lavado de manos?" to listOf(
            "a) Limpiar la piel para estética.",
            "b) Reducir la propagación de microorganismos.",
            "c) Evitar contacto directo con pacientes."
        ),
        "¿Qué es la asepsia quirúrgica?" to listOf(
            "a) Uso de técnicas avanzadas en procedimientos invasivos.",
            "b) Limpieza superficial de manos.",
            "c) Uso de alcohol en gel."
        ),
        "¿Qué es la asepsia médica?" to listOf(
            "a) Uso de técnicas avanzadas en cirugía.",
            "b) Procedimientos para reducir microorganismos en superficies y piel.",
            "c) Esterilización de instrumentos médicos."
        ),
        "¿Cuál de las siguientes es una técnica básica de asepsia?" to listOf(
            "a) Uso de equipo de protección personal.",
            "b) Administración de medicamentos.",
            "c) Diagnóstico clínico."
        ),
        "¿Qué equipo es fundamental para la asepsia quirúrgica?" to listOf(
            "a) Estetoscopio.",
            "b) Guantes estériles.",
            "c) Bata protectora."
        ),
        "¿Cuál es el tiempo recomendado para un lavado de manos efectivo?" to listOf(
            "a) Al menos 20 segundos.",
            "b) 5 segundos.",
            "c) 1 minuto."
        ),
        "¿Qué producto es esencial para la antisepsia en procedimientos médicos?" to listOf(
            "a) Jabón común.",
            "b) Solución de alcohol al 70%.",
            "c) Agua destilada."
        ),
        "¿Cuándo debe realizarse el lavado de manos en el ámbito hospitalario?" to listOf(
            "a) Solo antes de una cirugía.",
            "b) Antes y después de atender a cada paciente.",
            "c) Al inicio y fin de cada turno."
        )
    )

    val explanations = listOf(
        "El lavado de manos reduce significativamente la transmisión de microorganismos, protegiendo a pacientes y personal de salud.",
        "La asepsia quirúrgica incluye técnicas para mantener la esterilidad durante procedimientos invasivos.",
        "La asepsia médica se enfoca en reducir microorganismos en superficies, piel y equipos.",
        "El uso de equipo de protección personal es fundamental para prevenir la transmisión de patógenos.",
        "Los guantes estériles son esenciales para evitar la contaminación en procedimientos quirúrgicos.",
        "Un lavado de manos efectivo debe durar al menos 20 segundos para eliminar microorganismos.",
        "La solución de alcohol al 70% es altamente eficaz para la antisepsia en procedimientos médicos.",
        "El lavado de manos debe realizarse antes y después de atender a cada paciente para prevenir infecciones cruzadas."
    )


    var selectedAnswer by remember { mutableStateOf("") }
    var showFeedback by remember { mutableStateOf(false) }
    var showFinalSummary by remember { mutableStateOf(false) }

    if (showFinalSummary) {
        FinalSummary(score = score, totalQuestions = questions.size, onDismiss = onDismiss)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra de progreso
        ProgressBar(currentStep = currentQuestion + 1, totalSteps = questions.size)

        // Título del quiz
        Text(
            text = "Evaluación Rápida",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Pregunta actual
        Text(
            text = "Pregunta ${currentQuestion + 1} de ${questions.size}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = questions[currentQuestion].first,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        // Opciones de respuesta
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            questions[currentQuestion].second.forEach { answer ->
                Button(
                    onClick = {
                        selectedAnswer = answer
                        showFeedback = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedAnswer == answer)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(answer)
                }
            }
        }

        // Retroalimentación después de responder
        if (showFeedback) {
            FeedbackDialog(
                isCorrect = selectedAnswer == questions[currentQuestion].second[1],
                explanation = explanations[currentQuestion],
                onDismiss = {
                    showFeedback = false
                    if (selectedAnswer == questions[currentQuestion].second[1]) score++
                    if (currentQuestion < questions.size - 1) {
                        currentQuestion++
                        selectedAnswer = ""
                    } else {
                        showFinalSummary = true // Finaliza el quiz al terminar las preguntas
                    }
                }
            )
        }
    }
}

@Composable
fun FeedbackDialog(isCorrect: Boolean, explanation: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (isCorrect) "¡Correcto!" else "Incorrecto")
        },
        text = {
            Column {
                Text(
                    text = if (isCorrect) {
                        "¡Bien hecho! Comprendes el propósito de esta técnica."
                    } else {
                        "Respuesta incorrecta. La correcta es:\n${explanation.substringAfterLast(':').trim()}"
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = explanation)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}

@Composable
fun FinalSummary(score: Int, totalQuestions: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "¡Quiz Finalizado!")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Puntaje Final",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "$score de $totalQuestions respuestas correctas",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (score == totalQuestions) {
                        "¡Excelente trabajo! Dominaste este tema."
                    } else {
                        "Buen esfuerzo, sigue practicando para mejorar."
                    },
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}

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


