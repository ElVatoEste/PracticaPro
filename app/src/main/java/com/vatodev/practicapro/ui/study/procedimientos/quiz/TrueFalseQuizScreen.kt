package com.vatodev.practicapro.ui.study.procedimientos.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vatodev.practicapro.components.quizes.Feedback
import com.vatodev.practicapro.components.quizes.FinalSummary
import com.vatodev.practicapro.components.quizes.InstructionsDialog
import com.vatodev.practicapro.components.quizes.OpcionQuiz
import com.vatodev.practicapro.components.quizes.PantallaQuiz
import com.vatodev.practicapro.ui.study.asepsia.quiz.estadoDe
import com.vatodev.practicapro.viewmodel.NotesViewModel
import com.vatodev.practicapro.viewmodel.TrueFalseQuizViewModel
import kotlinx.coroutines.delay

/** Segunda evaluación de procedimientos, en formato verdadero/falso. */
private const val SUBJECT_ID = 5

@Composable
fun TrueFalseQuizScreen(
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val quizViewModel: TrueFalseQuizViewModel = viewModel()
    val notesViewModel: NotesViewModel = viewModel()

    val currentQuestion by quizViewModel.currentQuestion
    val score by quizViewModel.score
    val selectedAnswer by quizViewModel.selectedAnswer
    val showFeedback by quizViewModel.showFeedback
    val showFinalSummary by quizViewModel.showFinalSummary
    val showInstructions by quizViewModel.showInstructions
    val timeLeft by quizViewModel.timeLeft
    val maxTime = quizViewModel.maxTime

    var notaGuardada by remember { mutableStateOf(false) }

    if (showFinalSummary) {
        if (!notaGuardada) {
            notesViewModel.addNote(context, idMateria = SUBJECT_ID, puntaje = score)
            notaGuardada = true
        }
        FinalSummary(score = score, navController = navController)
        return
    }

    if (showInstructions) {
        InstructionsDialog(
            onStartClick = { quizViewModel.startQuiz() },
            onDismiss = onDismiss
        )
        return
    }

    LaunchedEffect(currentQuestion, showFeedback) {
        quizViewModel.resetTime()
        while (timeLeft > 0 && !showFeedback) {
            quizViewModel.reduceTime(0.1f)
            delay(100L)
        }
    }

    val pregunta = quizViewModel.questions[currentQuestion]
    val correcta = pregunta.options[pregunta.correctIndex]

    PantallaQuiz(
        preguntaActual = currentQuestion,
        totalPreguntas = quizViewModel.questions.size,
        tiempoRestante = timeLeft,
        tiempoMaximo = maxTime,
        enunciado = pregunta.text,
        onCerrar = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            pregunta.options.forEachIndexed { indice, opcion ->
                OpcionQuiz(
                    letra = if (indice == 0) "V" else "F",
                    texto = opcion,
                    estadoOpcion = estadoDe(opcion, correcta, selectedAnswer, showFeedback),
                    habilitada = selectedAnswer.isEmpty(),
                    onClick = { quizViewModel.selectAnswer(opcion) }
                )
            }
        }

        if (showFeedback) {
            Spacer(Modifier.height(20.dp))
            Feedback(
                isCorrect = selectedAnswer == correcta,
                explanation = quizViewModel.explanations[currentQuestion],
                timeBonus = quizViewModel.pointsAwarded,
                onNext = { quizViewModel.onDismissFeedback() }
            )
        }
    }
}
