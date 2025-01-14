package com.example.practicapro.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.practicapro.viewmodel.helper.Question

class QuizViewModel : ViewModel() {

    private val _currentQuestion = mutableIntStateOf(0)
    val currentQuestion: State<Int> get() = _currentQuestion

    private val _score = mutableIntStateOf(0)
    val score: State<Int> get() = _score

    private val _selectedAnswer = mutableStateOf("")
    val selectedAnswer: State<String> get() = _selectedAnswer

    private val _showFeedback = mutableStateOf(false)
    val showFeedback: State<Boolean> get() = _showFeedback

    private val _showFinalSummary = mutableStateOf(false)
    val showFinalSummary: State<Boolean> get() = _showFinalSummary

    private val _showInstructions = mutableStateOf(true)
    val showInstructions: State<Boolean> get() = _showInstructions

    private val _timeLeft = mutableFloatStateOf(10f)
    val timeLeft: State<Float> get() = _timeLeft

    private val _maxTime = mutableFloatStateOf(10f)
    val maxTime: Float get() = _maxTime.floatValue

    // Nueva variable para almacenar los puntos calculados
    private var lastPointsAwarded: Int = 0
    val pointsAwarded: Int get() = lastPointsAwarded

    fun startQuiz() {
        _timeLeft.floatValue = maxTime
        hideInstructions()
    }

    fun reduceTime(amount: Float = 0.1f) {
        if (_timeLeft.floatValue > 0 && !_showFeedback.value) {
            _timeLeft.floatValue = (_timeLeft.floatValue - amount).coerceAtLeast(0f)
        }
    }

    fun calculatePoints(timeLeft: Float): Int {
        val totalQuestions = _questions.size

        // Distribuir exactamente 100 puntos entre las preguntas
        val basePoints = 100 / totalQuestions // Valor base exacto de cada pregunta
        val remainingPoints = 100 % totalQuestions // Resto para compensar

        val timeSpent = maxTime - timeLeft

        Log.d("QuizDebug", "Total Questions: $totalQuestions")
        Log.d("QuizDebug", "Base Points: $basePoints")
        Log.d("QuizDebug", "Time Spent: $timeSpent")

        val points = when {
            timeSpent <= 4f -> basePoints // Puntos completos si se responde antes de 4s
            timeSpent <= maxTime -> {
                val penaltyFactor = 1f - ((timeSpent - 4f) / 6f) * 0.4f
                (basePoints * penaltyFactor).toInt()
            }
            else -> (basePoints * 0.6f).toInt()
        }

        // Agregar el resto de los puntos para garantizar que la suma total sea 100
        return points + if (_currentQuestion.intValue < remainingPoints) 1 else 0
    }

    fun selectAnswer(answer: String) {
        _selectedAnswer.value = answer
        _showFeedback.value = true

        val currentQ = _questions[_currentQuestion.intValue]
        val selectedIndex = currentQ.options.indexOf(_selectedAnswer.value)

        if (selectedIndex == currentQ.correctIndex) {
            lastPointsAwarded = calculatePoints(timeLeft.value)
            _score.value += lastPointsAwarded
        }
    }

    fun onDismissFeedback() {
        _showFeedback.value = false

        if (_currentQuestion.intValue < _questions.size - 1) {
            _currentQuestion.value += 1
            resetTime()
            _selectedAnswer.value = ""
        } else {
            _showFinalSummary.value = true
        }
    }


    fun resetTime() {
        _timeLeft.floatValue = maxTime
    }

    private fun hideInstructions() {
        _showInstructions.value = false
    }

    private fun createQuestion(
        text: String,
        rawOptions: List<String>
    ): Question {
        val correctOptionIndexInRaw = rawOptions.indexOfFirst { it.contains("(correcta)") }
        require(correctOptionIndexInRaw >= 0) {
            "Ninguna opción está marcada como '(correcta)' en la pregunta: $text"
        }

        val processedOptions = rawOptions.map { it.replace(" (correcta)", "") }
        val shuffledOptions = processedOptions.shuffled()
        val correctOption = processedOptions[correctOptionIndexInRaw]
        val correctIndexInShuffled = shuffledOptions.indexOf(correctOption)

        return Question(
            text = text,
            options = shuffledOptions,
            correctIndex = correctIndexInShuffled
        )
    }

    private val _questions = listOf(
        createQuestion(
            text = "¿Cuál es el primer paso para colocar los guantes de manera adecuada?",
            rawOptions = listOf(
                "Abrir el envoltorio externo.",
                "a Realizar higiene de manos. (correcta)",
                "Colocar los guantes con las palmas hacia abajo.",
                "Enguantarse la mano derecha."
            )
        ),
        createQuestion(
            text = "¿Cómo deben estar orientados los guantes cuando se colocan sobre el campo estéril?",
            rawOptions = listOf(
                "Las palmas de las manos deben mirar hacia abajo.",
                "a Los guantes deben estar con los pulgares hacia fuera y las palmas hacia arriba. (correcta)",
                "Los guantes deben estar en forma doblada.",
                "Los guantes deben estar completamente desordenados."
            )
        ),
        createQuestion(
            text = "Al colocar el guante en la mano derecha, ¿qué parte del guante debe tomar con la mano izquierda?",
            rawOptions = listOf(
                "a La parte del puño doblado hacia fuera. (correcta)",
                "La parte interna del guante.",
                "El dedo índice del guante.",
                "El centro del guante."
            )
        ),
        createQuestion(
            text = "Al colocar el guante en la mano izquierda, ¿con qué parte del guante debe tomarse?",
            rawOptions = listOf(
                "Con la palma de la mano enguantada por encima.",
                "a Por debajo del puño doblado hacia fuera. (correcta)",
                "Por el centro del guante.",
                "Tomarlo por los dedos."
            )
        ),
        createQuestion(
            text = "¿Qué se debe hacer con el puño de ambos guantes al final del proceso?",
            rawOptions = listOf(
                "Dejar el puño en su lugar original.",
                "a Dejar el puño doblado hacia fuera sin tocar la piel. (correcta)",
                "No importa cómo se deje el puño.",
                "Dejar el puño hacia dentro."
            )
        ),
        createQuestion(
            text = "¿Por qué se recomienda no tocar la parte externa del guante al colocarlos?",
            rawOptions = listOf(
                "Para no dañar el guante.",
                "a Para evitar la contaminación de los guantes estériles. (correcta)",
                "Porque el guante no está diseñado para ser tocado.",
                "Porque es más fácil colocarlos sin tocarlos."
            )
        )
    )
    val questions: List<Question> get() = _questions

    private val _explanations = listOf(
        "La higiene de manos es esencial para reducir la contaminación antes de colocar los guantes.",
        "Los guantes deben estar con los pulgares hacia fuera y las palmas hacia arriba para facilitar su colocación.",
        "La parte del puño doblado hacia fuera debe tomarse para evitar tocar la parte externa del guante.",
        "Al colocar el guante en la mano izquierda, debe tomarse por debajo del puño doblado hacia fuera.",
        "El puño debe dejarse doblado hacia fuera sin tocar la piel para mantener la esterilidad.",
        "No tocar la parte externa del guante es crucial para evitar la contaminación de los guantes estériles."
    )
    val explanations: List<String> get() = _explanations
}
