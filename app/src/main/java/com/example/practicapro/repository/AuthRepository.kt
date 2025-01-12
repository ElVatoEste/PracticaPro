package com.example.practicapro.repository

import android.content.Context
import com.example.practicapro.exceptions.EmailNotConfirmedException
import com.example.practicapro.model.*
import com.example.practicapro.network.ApiClient
import com.example.practicapro.service.AuthService
import com.example.practicapro.network.NetworkObserver
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import com.example.practicapro.rooms.entitys.User
import com.example.practicapro.utils.days
import com.example.practicapro.utils.minutes
import com.example.practicapro.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.google.gson.Gson

object AuthRepository {

    var useMockLogin: Boolean = false

    private val authService: AuthService by lazy {
        ApiClient.retrofit.create(AuthService::class.java)
    }

    suspend fun login(context: Context, email: String, password: String): Result<User> {
        return runCatching {
            if (useMockLogin) {
                mockLogin(email, password)
                    ?: throw Exception("Credenciales inválidas en modo Mock.")
            } else {
                val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
                if (!isNetworkAvailable) throw Exception("No hay conexión a internet.")
                val response = authService.login(LoginRequest(email, password))
                buildUser(response.user.nombre, response.user.email, response.accessToken)
            }
        }.recoverCatching { throwable ->
            handleHttpErrors(throwable)
        }.onSuccess { user ->
            saveUserToDatabase(context, user)
            ApiClient.setToken(user.token)
        }
    }

    // 📌 REGISTRO
    suspend fun register(
        context: Context,
        nombre: String,
        email: String,
        password: String
    ): Result<String> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (!isNetworkAvailable) throw Exception("No hay conexión a internet.")
            val response = authService.register(RegisterRequest(nombre, email, password))
            response.message
        }.recoverCatching { throwable ->
            handleHttpErrors(throwable).toString()
        }
    }

    // 📌 CONFIRMAR EMAIL
    suspend fun confirmEmail(context: Context, email: String, code: String): Result<String> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (!isNetworkAvailable) throw Exception("No hay conexión a internet.")
            val response = authService.confirmEmail(ConfirmationRequest(email, code))
            if (response.success) {
                response.message
            } else {
                throw Exception(response.message)
            }
        }.recoverCatching { throwable ->
            handleHttpErrors(throwable).toString()
        }
    }

    // 📌 REENVIAR EMAIL
    suspend fun resendVerificationEmail(email: String): Result<String> {
        return runCatching {
            val response = authService.resentEmail(EmailRequest(email))
            response.message
        }.recoverCatching { throwable ->
            handleHttpErrors(throwable).toString()
        }
    }

    // 📌 Construcción de objeto User
    private fun buildUser(nombre: String, email: String, token: String): User {
        val expirationDate = System.currentTimeMillis() + 7.days()
        return User(
            username = nombre,
            email = email,
            token = token,
            expirationDate = expirationDate
        )
    }

    // 🔄 Método de logout
    suspend fun logout(context: Context, userViewModel: UserViewModel) {
        val userDao = DatabaseProvider.getDatabase(context).userDao()
        withContext(Dispatchers.IO) {
            userDao.deleteUser()
        }
        userViewModel.clearUserProfile()
    }

    // 📌 Guardar usuario en la base de datos
    private suspend fun saveUserToDatabase(context: Context, user: User) {
        withContext(Dispatchers.IO) {
            val userDao = DatabaseProvider.getDatabase(context).userDao()
            userDao.insertUser(user)
        }
    }

    // 📌 Modo Mock
    private fun mockLogin(email: String, password: String): User? {
        return if (email == "test@test.com" && password == "password") {
            User(
                username = "Test User",
                email = "test@test.com",
                token = "mock_token_123",
                expirationDate = System.currentTimeMillis() + 5.minutes()
            )
        } else {
            null
        }
    }

    // 📌 Manejo de errores HTTP
    private fun handleHttpErrors(throwable: Throwable): User {
        when (throwable) {
            is java.net.UnknownHostException -> {
                throw Exception("No se pudo establecer conexión con el servidor. Por favor, inténtalo más tarde.")
            }

            is java.net.SocketTimeoutException -> {
                throw Exception("El servidor tardó demasiado en responder. Por favor, inténtalo más tarde.")
            }

            is HttpException -> {
                val statusCode = throwable.code()

                if (statusCode == 500) {
                    throw Exception("No se pudo completar la solicitud. Por favor, inténtalo más tarde.")
                }

                val errorBody = throwable.response()?.errorBody()?.string()
                val errorResponse = errorBody?.let { Gson().fromJson(it, LoginErrorResponse::class.java) }

                if (errorResponse != null) {
                    if (errorResponse.isConfirmed == false) {
                        throw EmailNotConfirmedException(errorResponse.message ?: "Correo no confirmado.")
                    }
                    throw Exception(errorResponse.message ?: "Error desconocido.")
                }

                throw Exception("Error HTTP: ${throwable.message()}")
            }

            else -> throw Exception("Error inesperado: ${throwable.message}")
        }
    }
}

