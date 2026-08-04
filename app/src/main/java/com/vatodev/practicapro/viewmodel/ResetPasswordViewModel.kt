package com.vatodev.practicapro.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vatodev.practicapro.repository.AuthRepository
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    // Estado para el correo al cual se enviará el código de reseteo
    private val _email = mutableStateOf("")
    val email get() = _email

    // Mensaje de error
    private val _error = mutableStateOf<String?>(null)
    val error get() = _error

    // Mensaje de éxito
    private val _successMessage = mutableStateOf("")
    val successMessage get() = _successMessage

    // Indicador de carga
    private val _isLoading = mutableStateOf(false)
    val isLoading get() = _isLoading

    // Actualizar el campo del correo
    fun onEmailChange(value: String) {
        _email.value = value
    }

    // Función para enviar el código de reseteo de contraseña
    fun sendResetCode(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            // Llamada al repositorio que se encargará de enviar el código
            val result = AuthRepository.sendResetPasswordCode(_email.value)
            _isLoading.value = false

            result.fold(
                onSuccess = { message ->
                    _successMessage.value = message
                    _error.value = null
                    onSuccess(message)
                },
                onFailure = {
                    _successMessage.value = ""
                    _error.value = it.localizedMessage
                    onError(it.localizedMessage ?: "Error desconocido")
                }
            )
        }
    }

    // Función para limpiar mensajes de error o éxito
    fun clearMessages() {
        _error.value = null
        _successMessage.value = ""
    }
}
