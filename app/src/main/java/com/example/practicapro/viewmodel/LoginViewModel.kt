package com.example.practicapro.ui.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicapro.exceptions.EmailNotConfirmedException
import com.example.practicapro.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Estados del formulario
    private val _email = mutableStateOf("")
    val email: State<String> get() = _email

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _showEmailNotConfirmedDialog = mutableStateOf(false)
    val showEmailNotConfirmedDialog: State<Boolean> get() = _showEmailNotConfirmedDialog

    // Funciones para actualizar los campos
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun dismissEmailNotConfirmedDialog() {
        _showEmailNotConfirmedDialog.value = false
        _error.value = null
    }

    // Lógica de login
    fun doLogin(
        context: Context,
        onLoginSuccess: () -> Unit,
        onErrorSnackBar: suspend (String) -> Unit
    ) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = AuthRepository.login(context, _email.value, _password.value)
            _isLoading.value = false

            result.fold(
                onSuccess = {
                    onLoginSuccess()
                },
                onFailure = { throwable ->
                    if (throwable is EmailNotConfirmedException) {
                        _error.value = throwable.message
                        _showEmailNotConfirmedDialog.value = true
                    } else {
                        Log.e("LoginViewModel", "Error en el inicio de sesión", throwable)
                        _error.value = "Error: ${throwable.localizedMessage}"
                    }
                    onErrorSnackBar(_error.value ?: "Ocurrió un error")
                }
            )
        }
    }
}
