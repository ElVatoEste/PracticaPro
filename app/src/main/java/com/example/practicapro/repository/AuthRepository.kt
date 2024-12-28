package com.example.practicapro.repository

import android.content.Context
import com.example.practicapro.model.LoginRequest
import com.example.practicapro.model.RegisterRequest
import com.example.practicapro.network.ApiClient
import com.example.practicapro.network.AuthService
import com.example.practicapro.network.NetworkObserver
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import com.example.practicapro.rooms.entitys.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException

object AuthRepository {

    var useMockLogin: Boolean = false

    private val authService: AuthService by lazy {
        ApiClient.retrofit.create(AuthService::class.java)
    }

    suspend fun login(context: Context, email: String, password: String): Result<User> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            val user = if (isNetworkAvailable && !useMockLogin) {
                val response = authService.login(LoginRequest(email, password))
                buildUser(response.user.nombre, response.user.email, response.accessToken)
            } else {
                mockLogin(email, password)
                    ?: error("Credenciales inválidas en modo mock.")
            }
            saveUserToDatabase(context, user)
            user
        }.mapCatching { user ->
            user
        }.recoverCatching { throwable ->
            handleHttpErrors(throwable)
        }
    }

    suspend fun register(context: Context, nombre: String, email: String, password: String): Result<User> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            check(isNetworkAvailable) { "No hay conexión a internet para registrar." }
            val response = authService.register(RegisterRequest(nombre, email, password))
            val user = buildUser(response.user.nombre, response.user.email, response.accessToken)
            saveUserToDatabase(context, user)
            user
        }.mapCatching { user ->
            user
        }.recoverCatching { throwable ->
            handleHttpErrors(throwable)
        }
    }

    private fun buildUser(nombre: String, email: String, token: String): User {
        val expirationDate = System.currentTimeMillis() + 6000
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

    private fun mockLogin(email: String, password: String): User? {
        return if (email == "test@test.com" && password == "password") {
            User(
                username = "TestUser",
                email = "test@test.com",
                token = "mock_token_123",
                expirationDate = System.currentTimeMillis() + 3600000
            )
        } else null
    }

    private fun handleHttpErrors(throwable: Throwable): User {
        when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> throw Exception("Solicitud inválida")
                    401 -> throw Exception("Correo o contraseña inválidos")
                    403 -> throw Exception("Acceso denegado")
                    409 -> throw Exception("El correo ya está registrado")
                    500 -> throw Exception("Error interno del servidor")
                }
                throw Exception("Error HTTP (${throwable.code()}): ${throwable.message()}")
            }
            else -> throw Exception("Error: ${throwable.message}")
        }
    }
}
