package com.vatodev.practicapro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PamViewModel : ViewModel() {

    private val _pamResult = MutableStateFlow<Double?>(null)
    val pamResult: StateFlow<Double?> get() = _pamResult

    private val _classification = MutableStateFlow<String?>(null)
    val classification: StateFlow<String?> get() = _classification

    // Función para calcular la Presión Arterial Media (PAM)
    fun calculatePam(ps: Double, pd: Double) {
        viewModelScope.launch {
            val pam = (2 * pd + ps) / 3
            _pamResult.value = pam
            _classification.value = getPamClassification(pam)
        }
    }

    // Clasificación de la PAM según el valor
    private fun getPamClassification(pam: Double): String {
        return when {
            pam < 70 -> "Presión Arterial Baja"
            pam in 70.0..110.0 -> "Presión Arterial Normal"
            else -> "Presión Arterial Alta"
        }
    }
}
