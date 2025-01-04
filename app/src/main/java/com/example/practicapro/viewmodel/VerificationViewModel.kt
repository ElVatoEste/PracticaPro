package com.example.practicapro.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicapro.repository.AuthRepository
import kotlinx.coroutines.launch

class VerificationViewModel : ViewModel() {

    // Estados
    private val _email = mutableStateOf("")
    val email get() = _email

    private val _code = mutableStateOf("")
    val code get() = _code

    private val _error = mutableStateOf<String?>(null)
    val error get() = _error

    private val _isLoading = mutableStateOf(false)
    val isLoading get() = _isLoading

    private val _isSuccess = mutableStateOf(false)
    val isSuccess get() = _isSuccess

    // Actualizar los campos
    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onCodeChange(value: String) {
        if (value.length <= 6) {
            _code.value = value
        }
    }

    // Confirmar el código
    fun confirmEmail(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = AuthRepository.confirmEmail(context, _email.value, _code.value)
            _isLoading.value = false

            result.fold(
                onSuccess = {
                    _isSuccess.value = true
                    _error.value = null
                },
                onFailure = {
                    _error.value = it.localizedMessage
                }
            )
        }
    }
}
