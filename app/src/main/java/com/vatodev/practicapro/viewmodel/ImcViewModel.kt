package com.vatodev.practicapro.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/** Una banda de clasificación. [hasta] es el límite superior, exclusivo. */
data class Banda(val etiqueta: String, val hasta: Double)

data class ResultadoImc(
    val imc: Double,
    val clasificacion: String,
    val bandas: List<Banda>,
    /** Peso que situaría a la persona dentro de la banda normal, en kg. */
    val pesoIdeal: ClosedRange<Double>
)

class ImcViewModel : ViewModel() {

    private val _resultado = mutableStateOf<ResultadoImc?>(null)
    val resultado: State<ResultadoImc?> = _resultado

    fun calcular(peso: Double, talla: Double, genero: String, edad: Int) {
        if (peso <= 0 || talla <= 0) return

        val imc = peso / (talla * talla)
        val bandas = bandasPara(genero, edad)
        val normal = bandas.indexOfFirst { it.etiqueta.startsWith("Peso normal") }
        val minNormal = if (normal <= 0) 0.0 else bandas[normal - 1].hasta
        val maxNormal = bandas[normal].hasta

        _resultado.value = ResultadoImc(
            imc = imc,
            clasificacion = bandas.firstOrNull { imc < it.hasta }?.etiqueta ?: bandas.last().etiqueta,
            bandas = bandas,
            pesoIdeal = (minNormal * talla * talla)..(maxNormal * talla * talla)
        )
    }

    fun limpiar() {
        _resultado.value = null
    }

    /**
     * Umbrales de clasificación. La escala de la interfaz consume esta misma
     * lista, así que lo que se ve coincide siempre con lo que se calcula.
     */
    private fun bandasPara(genero: String, edad: Int): List<Banda> = when {
        edad < 18 -> listOf(
            Banda("Bajo peso", 17.0),
            Banda("Peso normal", 25.0),
            Banda("Sobrepeso", 30.0),
            Banda("Obesidad", Double.MAX_VALUE)
        )

        edad >= 65 -> listOf(
            Banda("Bajo peso", 22.0),
            Banda("Peso normal", 27.0),
            Banda("Sobrepeso", Double.MAX_VALUE)
        )

        genero == "Mujer" -> listOf(
            Banda("Bajo peso", 18.0),
            Banda("Peso normal", 24.0),
            Banda("Sobrepeso", 29.0),
            Banda("Obesidad grado 1", 35.0),
            Banda("Obesidad grado 2", 40.0),
            Banda("Obesidad grado 3", Double.MAX_VALUE)
        )

        else -> listOf(
            Banda("Bajo peso", 18.5),
            Banda("Peso normal", 25.0),
            Banda("Sobrepeso", 30.0),
            Banda("Obesidad grado 1", 35.0),
            Banda("Obesidad grado 2", 40.0),
            Banda("Obesidad grado 3", Double.MAX_VALUE)
        )
    }
}
