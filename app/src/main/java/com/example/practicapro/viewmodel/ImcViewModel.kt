package com.example.practicapro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ImcViewModel : ViewModel() {

    private val _imcResult = MutableStateFlow<Double?>(null)
    val imcResult: StateFlow<Double?> get() = _imcResult

    private val _classification = MutableStateFlow<String?>(null)
    val classification: StateFlow<String?> get() = _classification

    fun calculateImc(weight: Double, height: Double, gender: String, age: Int) {
        viewModelScope.launch {
            val imc = weight / (height * height)
            _imcResult.value = imc
            _classification.value = getImcClassification(imc, gender, age)
        }
    }

    private fun getImcClassification(imc: Double, gender: String, age: Int): String {
        return when {
            // Clasificación para jóvenes menores de 18 años
            age < 18 -> when {
                imc < 17.0 -> "Bajo peso (adolescente)"
                imc in 17.0..24.99 -> "Peso normal (adolescente)"
                imc in 25.0..29.99 -> "Sobrepeso (adolescente)"
                else -> "Obesidad (adolescente)"
            }

            // Clasificación específica para hombres adultos
            gender == "Hombre" -> when {
                imc < 18.5 -> "Bajo peso (hombre)"
                imc in 18.5..24.99 -> "Peso normal (hombre)"
                imc in 25.0..29.99 -> "Sobrepeso (hombre)"
                imc in 30.0..34.99 -> "Obesidad grado 1 (hombre)"
                imc in 35.0..39.99 -> "Obesidad grado 2 (hombre)"
                else -> "Obesidad grado 3 (hombre)"
            }

            // Clasificación específica para mujeres adultas
            gender == "Mujer" -> when {
                imc < 18.0 -> "Bajo peso (mujer)"
                imc in 18.0..23.99 -> "Peso normal (mujer)"
                imc in 24.0..28.99 -> "Sobrepeso (mujer)"
                imc in 29.0..34.99 -> "Obesidad grado 1 (mujer)"
                imc in 35.0..39.99 -> "Obesidad grado 2 (mujer)"
                else -> "Obesidad grado 3 (mujer)"
            }

            // Clasificación general para mayores de 65 años
            age >= 65 -> when {
                imc < 22.0 -> "Bajo peso (mayores de 65)"
                imc in 22.0..27.0 -> "Peso normal (mayores de 65)"
                imc > 27.0 -> "Sobrepeso (mayores de 65)"
                else -> "Clasificación no disponible"
            }

            // Clasificación por defecto
            else -> "Clasificación no disponible"
        }
    }
}
