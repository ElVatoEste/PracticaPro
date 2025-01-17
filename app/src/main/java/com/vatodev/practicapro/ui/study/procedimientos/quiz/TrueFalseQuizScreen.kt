package com.vatodev.practicapro.ui.study.procedimientos.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vatodev.practicapro.components.quizes.AnimatedTimeBar
import com.vatodev.practicapro.components.quizes.Feedback
import com.vatodev.practicapro.components.quizes.FinalSummary
import com.vatodev.practicapro.components.quizes.InstructionsDialog
import com.vatodev.practicapro.components.quizes.ProgressBar
import com.vatodev.practicapro.viewmodel.NotesViewModel
import com.vatodev.practicapro.viewmodel.TrueFalseQuizViewModel
import kotlinx.coroutines.delay

@Composable
fun TrueFalseQuizScreen(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val tfQuizViewModel: TrueFalseQuizViewModel = viewModel()
    val notesViewModel: NotesViewModel = viewModel()

    val currentQuestion by tfQuizViewModel.currentQuestion
    val score by tfQuizViewModel.score
    val selectedAnswer by tfQuizViewModel.selectedAnswer
    val showFeedback by tfQuizViewModel.showFeedback
    val showFinalSummary by tfQuizViewModel.showFinalSummary
    val showInstructions by tfQuizViewModel.showInstructions
    val timeLeft by tfQuizViewModel.timeLeft
    val maxTime = tfQuizViewModel.maxTime

    var hasSentNote by remember { mutableStateOf(false) }

    if (showFinalSummary) {
        if (!hasSentNote) {
            notesViewModel.addNote(context, idMateria = 5, puntaje = score)
            hasSentNote = true
        }
        FinalSummary(
            score = score,
            onDismiss = onDismiss
        )
        return
    }

    if (showInstructions) {
        InstructionsDialog(
            onStartClick = { tfQuizViewModel.startQuiz() },
            onDismiss = onDismiss
        )
        return
    }

    LaunchedEffect(currentQuestion, showFeedback) {
        tfQuizViewModel.resetTime()
        while (timeLeft > 0 && !showFeedback) {
            tfQuizViewModel.reduceTime(0.1f)
            delay(100L)
        }
    }

    val currentQ = tfQuizViewModel.questions[currentQuestion]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressBar(
            currentStep = currentQuestion + 1,
            totalSteps = tfQuizViewModel.questions.size
        )

        AnimatedTimeBar(timeLeft = timeLeft, maxTime = maxTime)

        Text(
            text = "Evaluación Rápida",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Pregunta ${currentQuestion + 1} de ${tfQuizViewModel.questions.size}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = currentQ.text,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            currentQ.options.forEach { answer ->
                Button(
                    onClick = { tfQuizViewModel.selectAnswer(answer) },
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

        if (showFeedback) {
            val correctAnswer = currentQ.options[currentQ.correctIndex]
            Feedback(
                isCorrect = selectedAnswer == correctAnswer,
                explanation = tfQuizViewModel.explanations[currentQuestion],
                timeBonus = tfQuizViewModel.pointsAwarded,
                onNext = { tfQuizViewModel.onDismissFeedback() }
            )
        }
    }
}