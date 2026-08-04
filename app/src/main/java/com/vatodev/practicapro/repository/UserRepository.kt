package com.vatodev.practicapro.repository

import com.vatodev.practicapro.model.MessageResponse
import com.vatodev.practicapro.model.PasswordRequest
import com.vatodev.practicapro.model.UserProfileResponse
import com.vatodev.practicapro.network.ApiClient
import com.vatodev.practicapro.network.BackendGate
import com.vatodev.practicapro.service.UserService
import kotlinx.serialization.json.Json

object UserRepository {

    private val userService: UserService by lazy {
        ApiClient.retrofit.create(UserService::class.java)
    }

    suspend fun getUserProfile(): Result<UserProfileResponse> {
        return runCatching {
            if (!BackendGate.isReachable()) throw Exception("Servicio no disponible.")
            userService.getProfile()
        }.recoverCatching { throwable ->
            throw Exception("Error al obtener el perfil: ${throwable.message}")
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<MessageResponse> {
        return runCatching {
            if (!BackendGate.isReachable()) throw Exception("Servicio no disponible.")
            val request = PasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            userService.changePassword(request)
        }.recoverCatching { throwable ->
            if (throwable is retrofit2.HttpException) {
                val errorBody = throwable.response()?.errorBody()?.string()
                errorBody?.let { cuerpo ->
                    runCatching { Json { ignoreUnknownKeys = true }.decodeFromString<MessageResponse>(cuerpo) }
                        .getOrElse { MessageResponse(null, "Error al cambiar la contraseña", "Bad Request") }
                } ?: MessageResponse(null, "Error al cambiar la contraseña", "Bad Request")
            } else {
                throw Exception("Error al cambiar la contraseña: ${throwable.message}")
            }
        }
    }
}
