package com.example.practicapro.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.practicapro.model.UserProfileResponse

class UserViewModel : ViewModel() {

    // Token del usuario
    val token = mutableStateOf<String?>(null)

    // Datos del perfil de usuario
    val userProfile = mutableStateOf<UserProfileResponse?>(null)

    // Método para actualizar el perfil
    fun updateUserProfile(profile: UserProfileResponse) {
        userProfile.value = profile
    }

    // Actualizar el token
    fun updateToken(newToken: String) {
        token.value = newToken
    }

    // Método para limpiar el perfil (por ejemplo, al cerrar sesión)
    fun clearUserProfile() {
        userProfile.value = null
    }

}
