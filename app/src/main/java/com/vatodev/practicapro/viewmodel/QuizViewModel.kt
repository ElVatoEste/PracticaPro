package com.vatodev.practicapro.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.vatodev.practicapro.viewmodel.helper.Question

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

    private var lastPointsAwarded: Int = 0
    val pointsAwarded: Int get() = lastPointsAwarded

    private val _questions = mutableStateOf(emptyList<Question>())
    val questions: List<Question> get() = _questions.value

    private val _explanations = mutableStateOf(emptyList<String>())
    val explanations: List<String> get() = _explanations.value

    private val questionsPackage1 = listOf(
        createQuestion(
            "¿Cuál es el primer paso para colocar los guantes de manera adecuada?",
            listOf(
                "Abrir el envoltorio externo.",
                "Realizar higiene de manos. (correcta)",
                "Colocar los guantes con las palmas hacia abajo.",
                "Enguantarse la mano derecha."
            )
        ),
        createQuestion(
            "¿Cómo deben estar orientados los guantes cuando se colocan sobre el campo estéril?",
            listOf(
                "Las palmas de las manos deben mirar hacia abajo.",
                "Los guantes deben estar con los pulgares hacia fuera y las palmas hacia arriba. (correcta)",
                "Los guantes deben estar en forma doblada.",
                "Los guantes deben estar completamente desordenados."
            )
        ),
        createQuestion(
            "Al colocar el guante en la mano derecha, ¿qué parte del guante debe tomar con la mano izquierda?",
            listOf(
                "La parte del puño doblado hacia fuera. (correcta)",
                "La parte interna del guante.",
                "El dedo índice del guante.",
                "El centro del guante."
            )
        ),
        createQuestion(
            "Al colocar el guante en la mano izquierda, ¿con qué parte del guante debe tomarse?",
            listOf(
                "Con la palma de la mano enguantada por encima.",
                "Por debajo del puño doblado hacia fuera. (correcta)",
                "Por el centro del guante.",
                "Tomarlo por los dedos."
            )
        ),
        createQuestion(
            "¿Qué se debe hacer con el puño de ambos guantes al final del proceso?",
            listOf(
                "Dejar el puño en su lugar original.",
                "Dejar el puño doblado hacia fuera sin tocar la piel. (correcta)",
                "No importa cómo se deje el puño.",
                "Dejar el puño hacia dentro."
            )
        ),
        createQuestion(
            "¿Por qué se recomienda no tocar la parte externa del guante al colocarlos?",
            listOf(
                "Para no dañar el guante.",
                "Para evitar la contaminación de los guantes estériles. (correcta)",
                "Porque el guante no está diseñado para ser tocado.",
                "Porque es más fácil colocarlos sin tocarlos."
            )
        )
    )

    private val explanationsPackage1 = listOf(
        "La higiene de manos es esencial para reducir la contaminación antes de colocar los guantes.",
        "Los guantes deben estar con los pulgares hacia fuera y las palmas hacia arriba para facilitar su colocación.",
        "La parte del puño doblado hacia fuera debe tomarse para evitar tocar la parte externa del guante.",
        "Al colocar el guante en la mano izquierda, debe tomarse por debajo del puño doblado hacia fuera.",
        "El puño debe dejarse doblado hacia fuera sin tocar la piel para mantener la esterilidad.",
        "No tocar la parte externa del guante es crucial para evitar la contaminación de los guantes estériles."
    )

    private val questionsPackage2 = listOf(
        createQuestion(
            "¿Cuál es la acción inicial que debe realizarse antes de medir la frecuencia respiratoria de un paciente?",
            listOf(
                "Colocar al paciente en una posición adecuada. (correcta)",
                "Comprobar la identidad del paciente.",
                "Medir la frecuencia respiratoria al mismo tiempo que el pulso.",
                "Informar al paciente que se le va a medir la frecuencia respiratoria."
            )
        ),
        createQuestion(
            "¿Por qué no es conveniente informar al paciente que se le va a medir la frecuencia respiratoria?",
            listOf(
                "Porque el paciente podría alterar involuntariamente su ritmo respiratorio. (correcta)",
                "Porque la información podría generar ansiedad.",
                "Porque la medición debe realizarse de forma automática, sin que el paciente sepa.",
                "Porque podría afectar los resultados de otros signos vitales."
            )
        ),
        createQuestion(
            "¿Qué posición es recomendable para medir la frecuencia respiratoria?",
            listOf(
                "Posición de pie.",
                "Posición de semi-Fowler o acostado. (correcta)",
                "Posición lateral.",
                "Posición de Trendelenburg."
            )
        ),
        createQuestion(
            "¿Cuál de las siguientes opciones es correcta para medir la frecuencia respiratoria en un paciente?",
            listOf(
                "Contar las inspiraciones del paciente durante un minuto observando las elevaciones del tórax. (correcta)",
                "Usar únicamente la auscultación para contar las respiraciones.",
                "Contar únicamente las exhalaciones durante un minuto.",
                "Contar las respiraciones durante 30 segundos y multiplicar por 2."
            )
        ),
        createQuestion(
            "¿Qué características deben observarse además de la frecuencia respiratoria durante su medición?",
            listOf(
                "La presión arterial.",
                "El ritmo, profundidad y volumen de la respiración, así como el color de piel y uñas. (correcta)",
                "El peso y la temperatura corporal.",
                "El nivel de conciencia del paciente."
            )
        )
    )

    private val explanationsPackage2 = listOf(
        "Antes de cualquier medición, es clave colocar al paciente adecuadamente para obtener datos confiables.",
        "Informarle podría modificar su patrón respiratorio, alterando el resultado real.",
        "La posición semi-Fowler o acostado facilita la observación y asegura comodidad.",
        "Contar las inspiraciones durante un minuto completo permite mayor precisión en el conteo.",
        "Además del ritmo y la frecuencia, observa la profundidad, volumen y coloración de la piel para evaluar posibles alteraciones."
    )

    fun startQuiz(packageNumber: Int) {
        when (packageNumber) {
            1 -> {
                _questions.value = questionsPackage1
                _explanations.value = explanationsPackage1
            }
            2 -> {
                _questions.value = questionsPackage2
                _explanations.value = explanationsPackage2
            }
        }
        _timeLeft.floatValue = maxTime
        resetQuizState()
        hideInstructions()
    }

    fun reduceTime(amount: Float = 0.1f) {
        if (_timeLeft.floatValue > 0 && !_showFeedback.value) {
            _timeLeft.floatValue = (_timeLeft.floatValue - amount).coerceAtLeast(0f)
        }
    }

    private fun calculatePoints(timeLeft: Float): Int {
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
        _currentQuestion.intValue = 0
        _score.intValue = 0
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
        require(correctOptionIndexInRaw >= 0) { "Ninguna opción está marcada como '(correcta)': $text" }
        val processedOptions = rawOptions.map { it.replace(" (correcta)", "") }
        val shuffledOptions = processedOptions.shuffled()
        val correctOption = processedOptions[correctOptionIndexInRaw]
        val correctIndexInShuffled = shuffledOptions.indexOf(correctOption)
        return Question(text, shuffledOptions, correctIndexInShuffled)
    }
}
