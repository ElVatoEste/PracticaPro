package com.vatodev.practicapro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vatodev.practicapro.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    var successMessage by mutableStateOf("")
        private set

    /**
     * Llama al repositorio para cambiar la contraseña y maneja los estados.
     * Si el statusCode es 200, se muestra como éxito; si es 404 o 500 se muestra el error.
     * Para cualquier otro código, se muestra siempre el campo 'message' del response.
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            successMessage = ""

            val result = UserRepository.changePassword(currentPassword, newPassword)
            result.onSuccess { messageResponse ->
                when (messageResponse.statusCode) {
                    200 -> {
                        // Éxito: mostrar mensaje de éxito y luego limpiar
                        successMessage = messageResponse.message
                        delay(2000)
                        successMessage = ""
                    }
                    404, 500 -> {
                        // Para 404 o 500, mostramos el campo error (o "Error desconocido")
                        errorMessage = messageResponse.message  ?: "Error desconocido"
                    }
                    else -> {
                        // Para cualquier otro código, mostramos siempre el campo 'message'
                        errorMessage = messageResponse.message
                    }
                }
            }.onFailure { throwable ->
                errorMessage = throwable.message ?: "Error desconocido"
            }
            isLoading = false
        }
    }
}
