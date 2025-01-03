package com.example.practicapro.repository

import android.content.Context
import com.example.practicapro.exceptions.EmailNotConfirmedException
import com.example.practicapro.model.LoginRequest
import com.example.practicapro.model.RegisterRequest
import com.example.practicapro.model.LoginErrorResponse
import com.example.practicapro.network.ApiClient
import com.example.practicapro.network.AuthService
import com.example.practicapro.network.NetworkObserver
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import com.example.practicapro.rooms.entitys.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.google.gson.Gson

object AuthRepository {

    private val authService: AuthService by lazy {
        ApiClient.retrofit.create(AuthService::class.java)
    }

    suspend fun login(context: Context, email: String, password: String): Result<User> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (!isNetworkAvailable) throw Exception("No hay conexión a internet.")

            val response = authService.login(LoginRequest(email, password))

            val user = buildUser(response.user.nombre, response.user.email, response.accessToken)
            saveUserToDatabase(context, user)
            user
        }.recoverCatching { throwable ->
            handleHttpErrors(throwable)
        }
    }

    suspend fun register(context: Context, nombre: String, email: String, password: String): Result<User> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (!isNetworkAvailable) throw Exception("No hay conexión a internet.")

            val response = authService.register(RegisterRequest(nombre, email, password))

            // Crear usuario con la respuesta exitosa
            val user = buildUser(response.user.nombre, response.user.email, response.accessToken)
            saveUserToDatabase(context, user)
            user
        }.recoverCatching { throwable ->
            handleHttpErrors(throwable)
        }
    }

    private fun buildUser(nombre: String, email: String, token: String): User {
        val expirationDate = System.currentTimeMillis() + 3600000
        return User(
            username = nombre,
            email = email,
            token = token,
            expirationDate = expirationDate
        )
    }

    private suspend fun saveUserToDatabase(context: Context, user: User) {
        withContext(Dispatchers.IO) {
            val userDao = DatabaseProvider.getDatabase(context).userDao()
            userDao.insertUser(user)
        }
    }

    private fun handleHttpErrors(throwable: Throwable): User {
        when (throwable) {
            is HttpException -> {
                val errorBody = throwable.response()?.errorBody()?.string()
                val errorResponse = errorBody?.let { Gson().fromJson(it, LoginErrorResponse::class.java) }

                if (errorResponse != null) {
                    // Si el servidor indica que el correo no está confirmado
                    if (errorResponse.isConfirmed == false) {
                        throw EmailNotConfirmedException(errorResponse.message ?: "Correo electrónico no confirmado.")
                    }
                    // Aquí puedes manejar otros tipos de errores que vengan en el errorResponse
                    throw Exception(errorResponse.message ?: "Error desconocido.")
                }

                throw Exception("Error HTTP (${throwable.code()}): ${throwable.message()}")
            }
            else -> throw Exception("Error inesperado: ${throwable.message}")
        }
    }

}
