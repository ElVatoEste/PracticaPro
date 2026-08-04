package com.vatodev.practicapro.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class ResultadoPam(
    val pam: Double,
    val clasificacion: String,
    val bandas: List<Banda>,
    val sistolica: Double,
    val diastolica: Double
)

class PamViewModel : ViewModel() {

    private val _resultado = mutableStateOf<ResultadoPam?>(null)
    val resultado: State<ResultadoPam?> = _resultado

    /**
     * PAM = (2 · diastólica + sistólica) / 3.
     *
     * El umbral inferior de 60 mmHg es el de perfusión de órganos: por debajo
     * deja de garantizarse y por eso la banda se marca como crítica.
     */
    fun calcular(sistolica: Double, diastolica: Double) {
        if (sistolica <= 0 || diastolica <= 0 || diastolica >= sistolica) return

        val pam = (2 * diastolica + sistolica) / 3
        _resultado.value = ResultadoPam(
            pam = pam,
            clasificacion = BANDAS.firstOrNull { pam < it.hasta }?.etiqueta ?: BANDAS.last().etiqueta,
            bandas = BANDAS,
            sistolica = sistolica,
            diastolica = diastolica
        )
    }

    companion object {
        val BANDAS = listOf(
            Banda("Hipoperfusión", 60.0),
            Banda("Baja", 70.0),
            Banda("Normal", 100.0),
            Banda("Elevada", 110.0),
            Banda("Alta", Double.MAX_VALUE)
        )
    }
}
