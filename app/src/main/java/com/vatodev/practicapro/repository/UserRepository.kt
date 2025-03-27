package com.vatodev.practicapro.repository

import com.vatodev.practicapro.model.MessageResponse
import com.vatodev.practicapro.model.PasswordRequest
import com.vatodev.practicapro.model.UserProfileResponse
import com.vatodev.practicapro.network.ApiClient
import com.vatodev.practicapro.network.NetworkObserver
import com.vatodev.practicapro.service.UserService
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

    // Cambiar la contraseña del usuario
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<MessageResponse> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (!isNetworkAvailable) throw Exception("No hay conexión a internet.")
            val request = PasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            userService.changePassword(request)
        }.recoverCatching { throwable ->
            if (throwable is retrofit2.HttpException) {
                val errorBody = throwable.response()?.errorBody()?.string()
                val gson = com.google.gson.Gson()
                errorBody?.let {
                    try {
                        gson.fromJson(it, MessageResponse::class.java)
                    } catch (e: Exception) {
                        MessageResponse(null, "Error al cambiar la contraseña", "Bad Request")
                    }
                } ?: MessageResponse(null, "Error al cambiar la contraseña", "Bad Request")
            } else {
                throw Exception("Error al cambiar la contraseña: ${throwable.message}")
            }
        }
    }
}
