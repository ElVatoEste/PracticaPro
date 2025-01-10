package com.example.practicapro.repository

import android.content.Context
import com.example.practicapro.model.UserProfileResponse
import com.example.practicapro.network.ApiClient
import com.example.practicapro.network.NetworkObserver
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import com.example.practicapro.service.UserService
import com.example.practicapro.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

object UserRepository {

    private val userService: UserService by lazy {
        ApiClient.retrofit.create(UserService::class.java)
    }

    // 📌 Inyectar el ViewModel global
    lateinit var userViewModel: UserViewModel

    // 📌 Obtener el token desde Room y almacenarlo en el ViewModel
    suspend fun loadTokenFromRoom(context: Context) {
        val userDao = DatabaseProvider.getDatabase(context).userDao()
        val localUser = withContext(Dispatchers.IO) { userDao.getUser() }

        if (localUser != null) {
            userViewModel.updateToken(localUser.token)
        }
    }

    // 📌 Obtener el perfil del usuario desde la API
    suspend fun getUserProfileFromApi(): UserProfileResponse {
        val token = userViewModel.token.value
            ?: throw Exception("No se encontró un token válido.")

        val bearerToken = "Bearer $token"
        val response = userService.getProfile(bearerToken)

        // Actualizar el ViewModel con los datos recibidos
        userViewModel.updateUserProfile(
            UserProfileResponse(
                id = response.id,
                nombre = response.nombre,
                email = response.email
            )
        )

        return response
    }

    // 📌 Obtener el perfil del usuario con sincronización Offline-First
    suspend fun getUserProfile(context: Context): Result<UserProfileResponse> {
        return runCatching {
            // 1️⃣ Verificar si el perfil ya está en el ViewModel
            userViewModel.userProfile.value?.let {
                return@runCatching it
            }

            // 2️⃣ Cargar el token desde Room si no está en el ViewModel
            loadTokenFromRoom(context)

            // 3️⃣ Si hay conexión a internet, sincronizar con la API
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (isNetworkAvailable) {
                return@runCatching getUserProfileFromApi()
            } else {
                throw Exception("No hay conexión a internet y no se pudo obtener el perfil.")
            }
        }.recoverCatching { throwable ->
            throw Exception("Error al obtener el perfil: ${throwable.message}")
        }
    }
}
