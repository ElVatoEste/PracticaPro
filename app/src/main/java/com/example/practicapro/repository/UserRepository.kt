package com.example.practicapro.repository

import com.example.practicapro.model.UserProfileResponse
import com.example.practicapro.network.ApiClient
import com.example.practicapro.network.NetworkObserver
import com.example.practicapro.service.UserService
import kotlinx.coroutines.flow.first

object UserRepository {

    private val userService: UserService by lazy {
        ApiClient.retrofit.create(UserService::class.java)
    }

    // Obtener el perfil del usuario desde la API
    suspend fun getUserProfile(): Result<UserProfileResponse> {
        return runCatching {
            // Verificar si hay conexión a internet
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (!isNetworkAvailable) throw Exception("No hay conexión a internet.")

            // Llamar a la API
            userService.getProfile()
        }.recoverCatching { throwable ->
            throw Exception("Error al obtener el perfil: ${throwable.message}")
        }
    }
}
