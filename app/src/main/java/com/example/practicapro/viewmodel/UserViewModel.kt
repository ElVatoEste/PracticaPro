package com.example.practicapro.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.practicapro.model.UserProfileResponse
import com.example.practicapro.network.ApiClient
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {

    // Token del usuario
    val token = mutableStateOf<String?>(null)

    // Perfil del usuario
    val userProfile = mutableStateOf<UserProfileResponse?>(null)

    // Actualizar el token
    fun updateToken(newToken: String) {
        token.value = newToken
        ApiClient.setToken(newToken)  // Actualiza el ApiClient
    }

    // Limpiar datos del perfil (por ejemplo, al cerrar sesión)
    fun clearUserProfile() {
        token.value = null
        userProfile.value = null
        ApiClient.setToken(null)  // Limpia el token en el ApiClient
    }

    // 🔄 Cargar el token desde Room
    suspend fun loadTokenFromRoom(context: Context) {
        val userDao = DatabaseProvider.getDatabase(context).userDao()
        val user = withContext(Dispatchers.IO) {
            userDao.getUser()
        }
        user?.token?.let { updateToken(it) }
    }
}
