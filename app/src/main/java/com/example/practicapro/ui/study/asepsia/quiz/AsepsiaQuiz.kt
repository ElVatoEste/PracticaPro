package com.example.practicapro.ui.study.asepsia.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicapro.components.quizes.*
import com.example.practicapro.viewmodel.NotesViewModel
import com.example.practicapro.viewmodel.QuizViewModel
import kotlinx.coroutines.delay


@Composable
fun QuizScreen(
    onDismiss: () -> Unit,
    quizViewModel: QuizViewModel = viewModel(),
    notesViewModel: NotesViewModel = viewModel()
) {
    // Obtener el contexto desde LocalContext
    val context = LocalContext.current

    // Estados del ViewModel
    val currentQuestion by quizViewModel.currentQuestion
    val score by quizViewModel.score
    val selectedAnswer by quizViewModel.selectedAnswer
    val showFeedback by quizViewModel.showFeedback
    val showFinalSummary by quizViewModel.showFinalSummary
    val showInstructions by quizViewModel.showInstructions
    val timeLeft by quizViewModel.timeLeft
    val maxTime = quizViewModel.maxTime

    // Estado para controlar si se ha enviado la nota
    var hasSentNote by remember { mutableStateOf(false) }

    if (showFinalSummary) {
        if (!hasSentNote) {
            notesViewModel.addNote(context ,idMateria = 1, puntaje = score)
            hasSentNote = true
        }

        FinalSummary(
            score = score,
            onDismiss = onDismiss
        )
        return
    }

    // Mostrar instrucciones antes de iniciar
    if (showInstructions) {
        InstructionsDialog(
            onStartClick = { quizViewModel.startQuiz() },
            onDismiss = onDismiss
        )
        return
    }

    // Manejo de tiempo
    LaunchedEffect(currentQuestion, showFeedback) {
        quizViewModel.resetTime()
        while (timeLeft > 0 && !showFeedback) {
            quizViewModel.reduceTime(0.1f)
            delay(100L)
        }
    }

    // Contenido del Quiz
    val currentQ = quizViewModel.questions[currentQuestion]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra de progreso
        ProgressBar(
            currentStep = currentQuestion + 1,
            totalSteps = quizViewModel.questions.size
        )

        // Barra de tiempo
        AnimatedTimeBar(timeLeft = timeLeft, maxTime = maxTime)

        // Título
        Text(
            text = "Evaluación Rápida",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Pregunta actual
        Text(
            text = "Pregunta ${currentQuestion + 1} de ${quizViewModel.questions.size}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = currentQ.text,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        // Opciones de respuesta
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            currentQ.options.forEach { answer ->
                Button(
                    onClick = { quizViewModel.selectAnswer(answer) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAnswer.isEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            selectedAnswer == answer -> MaterialTheme.colorScheme.secondary
                            selectedAnswer.isNotEmpty() -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                ) {
                    Text(answer)
                }
            }
        }

        // Feedback
        if (showFeedback) {
            val correctAnswer = currentQ.options[currentQ.correctIndex]

            Feedback(
                isCorrect = selectedAnswer == correctAnswer,
                explanation = quizViewModel.explanations[currentQuestion],
                timeBonus = quizViewModel.pointsAwarded,
                onNext = { quizViewModel.onDismissFeedback() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    QuizScreen(onDismiss = {})
}