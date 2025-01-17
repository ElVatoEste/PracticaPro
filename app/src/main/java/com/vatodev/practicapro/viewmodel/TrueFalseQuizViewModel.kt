package com.vatodev.practicapro.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.vatodev.practicapro.viewmodel.helper.Question

class TrueFalseQuizViewModel : ViewModel() {

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

    private var lastPointsAwarded: Int = 0
    val pointsAwarded: Int get() = lastPointsAwarded

    private val _questions = listOf(
        createQuestion(
            "Se debe ayudar al paciente a adoptar una posición cómoda que permita el acceso a la zona seleccionada para la medición del pulso.",
            listOf("Verdadero (correcta)", "Falso")
        ),
        createQuestion(
            "Los lugares más comunes para medir la frecuencia cardíaca son la arteria radial, braquial, carótida, temporal, femoral, tibial posterior, poplítea o pedia.",
            listOf("Verdadero (correcta)", "Falso")
        ),
        createQuestion(
            "Para localizar el latido arterial, se deben usar las yemas de los dedos índice, corazón y anular de la mano dominante. El uso del pulgar está contraindicado porque tiene pulso propio.",
            listOf("Verdadero (correcta)", "Falso")
        ),
        createQuestion(
            "Al valorar el pulso, se debe observar solo la amplitud (fuerte o débil), sin tomar en cuenta el ritmo (regular o irregular) ni la tensión (blando o duro).",
            listOf("Verdadero", "Falso (correcta)")
        )
    )
    val questions: List<Question> get() = _questions

    private val _explanations = listOf(
        "Es importante que el paciente esté cómodo y que la zona a explorar sea accesible.",
        "Son sitios anatómicos comunes para palpar el pulso con precisión.",
        "El pulgar tiene pulso propio; por ello se usan otros dedos para evitar confusiones.",
        "También deben observarse el ritmo (regular o irregular) y la tensión (blando o duro)."
    )
    val explanations: List<String> get() = _explanations

    fun startQuiz() {
        resetQuizState()
        hideInstructions()
    }

    fun reduceTime(amount: Float = 0.1f) {
        if (_timeLeft.floatValue > 0 && !_showFeedback.value) {
            _timeLeft.floatValue = (_timeLeft.floatValue - amount).coerceAtLeast(0f)
        }
    }

    fun calculatePoints(timeLeft: Float): Int {
        val totalQuestions = questions.size
        val basePoints = 100 / totalQuestions
        val remainingPoints = 100 % totalQuestions
        val timeSpent = maxTime - timeLeft

        val points = when {
            timeSpent <= 4f -> basePoints
            timeSpent <= maxTime -> {
                val penaltyFactor = 1f - ((timeSpent - 4f) / 6f) * 0.4f
                (basePoints * penaltyFactor).toInt()
            }
            else -> (basePoints * 0.6f).toInt()
        }

        return if (remainingPoints > 0) {
            points + if (_currentQuestion.intValue < remainingPoints) 1 else 0
        } else {
            points
        }
    }

    fun selectAnswer(answer: String) {
        _selectedAnswer.value = answer
        _showFeedback.value = true
        val currentQ = questions[_currentQuestion.intValue]
        val selectedIndex = currentQ.options.indexOf(_selectedAnswer.value)
        if (selectedIndex == currentQ.correctIndex) {
            lastPointsAwarded = calculatePoints(timeLeft.value)
            _score.value += lastPointsAwarded
        }
    }

    fun onDismissFeedback() {
        _showFeedback.value = false
        if (_currentQuestion.intValue < questions.size - 1) {
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

    private fun resetQuizState() {
        _currentQuestion.value = 0
        _score.value = 0
        _selectedAnswer.value = ""
        _showFeedback.value = false
        _showFinalSummary.value = false
        _timeLeft.floatValue = maxTime
    }

    private fun hideInstructions() {
        _showInstructions.value = false
    }

    private fun createQuestion(text: String, rawOptions: List<String>): Question {
        val correctOptionIndexInRaw = rawOptions.indexOfFirst { it.contains("(correcta)") }
        require(correctOptionIndexInRaw >= 0) {
            "Ninguna opción está marcada como '(correcta)': $text"
        }
        val processedOptions = rawOptions.map { it.replace(" (correcta)", "") }
        val shuffledOptions = processedOptions.shuffled()
        val correctOption = processedOptions[correctOptionIndexInRaw]
        val correctIndexInShuffled = shuffledOptions.indexOf(correctOption)
        return Question(text, shuffledOptions, correctIndexInShuffled)
    }
}
