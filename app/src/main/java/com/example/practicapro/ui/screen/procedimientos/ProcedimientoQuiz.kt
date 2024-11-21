package com.example.practicapro.ui.screen.procedimientos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicapro.ui.screen.asepsia.ProgressBar
import kotlinx.coroutines.delay

@Composable
fun ProcedimientosQuizScreen(onDismiss: () -> Unit) {
    var currentQuestion by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var showFinalSummary by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(10f) }
    val maxTime = 10f

    val questions = listOf(
        "¿Cuál es el primer paso al medir la presión arterial?" to listOf(
            "a) Colocar el brazalete en el brazo del paciente.",
            "b) Asegurarse de que el paciente esté en posición cómoda.",
            "c) Inflar el brazalete rápidamente."
        ),
        "¿Qué es importante al tomar el pulso radial?" to listOf(
            "a) Utilizar el pulgar para sentir el pulso.",
            "b) Contar las pulsaciones durante 15 segundos y multiplicar por 4.",
            "c) Aplicar presión fuerte sobre la arteria."
        ),
        "¿Cómo se debe limpiar una herida antes de aplicar un vendaje?" to listOf(
            "a) Frotar con fuerza para eliminar toda suciedad.",
            "b) Limpiar suavemente desde el centro hacia los bordes.",
            "c) Aplicar solución salina directamente sobre la herida y cubrir."
        ),
        "¿Qué equipo es fundamental para prevenir infecciones al realizar procedimientos básicos?" to listOf(
            "a) Alcohol al 70%.",
            "b) Guantes y mascarilla.",
            "c) Jabón común."
        ),
        "¿Cómo se debe realizar la auscultación con un estetoscopio?" to listOf(
            "a) Colocar el estetoscopio directamente sobre la ropa del paciente.",
            "b) Asegurarse de que el paciente respire profundamente varias veces.",
            "c) Utilizar el estetoscopio en un ambiente ruidoso."
        ),
        "¿Cuál es el ángulo recomendado al administrar una inyección intramuscular?" to listOf(
            "a) 45 grados.",
            "b) 90 grados.",
            "c) 15 grados."
        ),
        "¿Qué paso es fundamental antes de realizar un procedimiento invasivo?" to listOf(
            "a) Esterilizar el equipo y el área a tratar.",
            "b) Consultar el historial médico del paciente.",
            "c) Informar al paciente sobre los riesgos."
        ),
        "¿Qué indicador clave se utiliza para evaluar la calidad del lavado de manos?" to listOf(
            "a) La cantidad de espuma producida.",
            "b) La duración del lavado.",
            "c) La eliminación visible de suciedad."
        ),
        "¿Qué posición es adecuada para un paciente en shock?" to listOf(
            "a) Posición de Trendelenburg.",
            "b) Sentado con las piernas cruzadas.",
            "c) Acostado con las piernas elevadas."
        ),
        "¿Cómo se asegura una lectura precisa al medir la temperatura corporal?" to listOf(
            "a) Usar el termómetro inmediatamente después de sacarlo del envase.",
            "b) Colocar el termómetro en contacto directo con el cuerpo sin obstrucciones.",
            "c) Limpiar el termómetro después de cada uso."
        )
    )

    val correctAnswers = listOf(
        questions[0].second[1], // Asegurarse de que el paciente esté en posición cómoda.
        questions[1].second[1], // Contar las pulsaciones durante 15 segundos y multiplicar por 4.
        questions[2].second[1], // Limpiar suavemente desde el centro hacia los bordes.
        questions[3].second[1], // Guantes y mascarilla.
        questions[4].second[1], // Asegurarse de que el paciente respire profundamente varias veces.
        questions[5].second[1], // 90 grados.
        questions[6].second[0], // Esterilizar el equipo y el área a tratar.
        questions[7].second[1], // La duración del lavado.
        questions[8].second[2], // Acostado con las piernas elevadas.
        questions[9].second[1]  // Colocar el termómetro en contacto directo con el cuerpo.
    )

    val explanations = listOf(
        "El paciente debe estar en posición cómoda y relajada para obtener una medición precisa.",
        "El pulgar no debe usarse para medir el pulso. Contar durante 15 segundos es un método común.",
        "La limpieza debe hacerse suavemente desde el centro hacia los bordes para evitar contaminación.",
        "Los guantes y mascarillas son esenciales para prevenir infecciones durante procedimientos.",
        "El estetoscopio debe colocarse sobre la piel del paciente, evitando interferencias.",
        "La inyección intramuscular debe administrarse a 90 grados para una correcta absorción.",
        "Esterilizar el equipo y el área de trabajo reduce significativamente el riesgo de infecciones.",
        "La duración adecuada asegura una limpieza efectiva, eliminando microorganismos.",
        "Acostar al paciente con las piernas elevadas mejora el flujo sanguíneo en situaciones de shock.",
        "El contacto directo del termómetro con la piel asegura una lectura precisa de la temperatura."
    )

    if (showFinalSummary) {
        FinalSummary(score = score, totalQuestions = questions.size, onDismiss = onDismiss)
        return
    }

    LaunchedEffect(currentQuestion, timeLeft) {
        if (timeLeft > 0 && !showFeedback) {
            delay(100L) // Resta 0.1 segundos
            timeLeft -= 0.1f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressBar(currentStep = currentQuestion + 1, totalSteps = questions.size)

        // Barra de tiempo con suavizado
        AnimatedTimeBar(timeLeft = timeLeft, maxTime = maxTime)

        // Título
        Text(
            text = "Quiz: Procedimientos Básicos",
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
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !showFeedback
                ) {
                    Text(answer)
                }
            }
        }

        // Retroalimentación
        AnimatedVisibility(visible = showFeedback) {
            Feedback(
                isCorrect = selectedAnswer?.let { it == correctAnswers[currentQuestion] },
                explanation = explanations[currentQuestion],
                timeBonus = (timeLeft / maxTime * 2).toInt(),
                onNext = {
                    if (selectedAnswer == correctAnswers[currentQuestion]) {
                        score += 1 + (timeLeft / maxTime * 2).toInt()
                    }
                    if (currentQuestion < questions.size - 1) {
                        currentQuestion++
                        timeLeft = maxTime
                        selectedAnswer = null // Resetea para evitar estados incorrectos
                        showFeedback = false
                    } else {
                        showFinalSummary = true
                    }
                }
            )
        }
    }
}

@Composable
fun AnimatedTimeBar(timeLeft: Float, maxTime: Float) {
    val animatedTime = remember { Animatable(timeLeft) }
    LaunchedEffect(timeLeft) {
        animatedTime.animateTo(timeLeft)
    }
    LinearProgressIndicator(
        progress = animatedTime.value / maxTime,
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp),
        color = if (timeLeft > maxTime * 0.3f) Color(0xFF7DBB00) else Color(0xFFFF5252)
    )
}

@Composable
fun Feedback(isCorrect: Boolean?, explanation: String, timeBonus: Int, onNext: () -> Unit) {
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
            Icon(
                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isCorrect) {
                    "¡Correcto! +$timeBonus puntos extra"
                } else {
                    "Incorrecto. Intenta de nuevo."
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = explanation,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun FinalSummary(score: Int, totalQuestions: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "¡Quiz Finalizado!")
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Puntaje Final: $score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (score == totalQuestions * 2) {
                        "¡Perfecto! Has dominado este tema al máximo."
                    } else if (score > totalQuestions) {
                        "¡Buen trabajo! Puedes intentar mejorar aún más."
                    } else {
                        "Sigue practicando para alcanzar tu mejor desempeño."
                    },
                    fontSize = 16.sp,
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