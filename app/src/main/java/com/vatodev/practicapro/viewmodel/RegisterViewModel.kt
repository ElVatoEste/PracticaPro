package com.vatodev.practicapro.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vatodev.practicapro.repository.AuthRepository
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


    fun onNombreChange(value: String) { _nombre.value = value }
    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = value }


    fun doRegister(context: Context, onRegisterSuccess: (String) -> Unit) {
        viewModelScope.launch {
            if (_password.value != _confirmPassword.value) {
                _error.value = "Las contraseñas no coinciden."
                return@launch
            }

            _isLoading.value = true
            val resultado = AuthRepository.registrar(
                context = context,
                nombre = _nombre.value,
                email = _email.value,
                password = _password.value
            )
            _isLoading.value = false

            resultado.fold(
                onSuccess = { usuario ->
                    _error.value = null
                    onRegisterSuccess("Cuenta de ${usuario.username} creada.")
                },
                onFailure = { _error.value = it.message ?: "No se pudo crear la cuenta." }
            )
        }
    }
}
