package com.example.practicapro.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicapro.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _nombre = mutableStateOf("")
    val nombre: State<String> get() = _nombre

    private val _email = mutableStateOf("")
    val email: State<String> get() = _email

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> get() = _confirmPassword

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _showRegisterModal = mutableStateOf(false)
    val showRegisterModal: State<Boolean> get() = _showRegisterModal

    // Actualizar campos
    fun onNombreChange(value: String) { _nombre.value = value }
    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = value }

    // Cerrar modal sin registrar
    fun onCancelRegister() {
        _showRegisterModal.value = false
        _error.value = null
    }

    // Ejecutar el registro real
    fun doRegister(
        context: Context,
        onRegisterSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (_password.value != _confirmPassword.value) {
                _error.value = "Las contraseñas no coinciden"
                return@launch
            }

            _isLoading.value = true
            val result = AuthRepository.register(
                context,
                _nombre.value,
                _email.value,
                _password.value
            )
            _isLoading.value = false

            result.fold(
                onSuccess = { message ->
                    _showRegisterModal.value = false
                    _error.value = null
                    onRegisterSuccess(message)
                },
                onFailure = {
                    _error.value = "Error: ${it.localizedMessage}"
                }
            )
        }
    }
}
