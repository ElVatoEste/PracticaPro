package com.vatodev.practicapro.repository

import android.content.Context
import com.vatodev.practicapro.network.ApiClient
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.User
import com.vatodev.practicapro.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthRepository {

    suspend fun login(context: Context, email: String, password: String): Result<User> {
        return Result.failure(Exception("El login está deshabilitado en modo offline."))
    }

    suspend fun register(
        context: Context,
        nombre: String,
        email: String,
        password: String
    ): Result<String> {
        return runCatching {
            if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                throw Exception("Todos los campos son obligatorios.")
            }
            val user = buildLocalUser(nombre.trim(), email.trim())
            saveUserToDatabase(context, user)
            ApiClient.setToken(user.token)
            "Registro local completado."
        }
    }

    suspend fun sendResetPasswordCode(email: String): Result<String> {
        return Result.failure(Exception("Función no disponible en modo offline."))
    }

    suspend fun confirmEmail(email: String, code: String): Result<String> {
        return Result.failure(Exception("Función no disponible en modo offline."))
    }

    suspend fun resendVerificationEmail(email: String): Result<String> {
        return Result.failure(Exception("Función no disponible en modo offline."))
    }

    suspend fun logout(context: Context, userViewModel: UserViewModel) {
        val database = DatabaseProvider.getDatabase(context)
        val userDao = database.userDao()
        val noteDao = database.noteDao()

        withContext(Dispatchers.IO) {
            userDao.deleteUser()
            noteDao.deleteAllNotes()
        }

        userViewModel.clearUserProfile()
    }

    private suspend fun saveUserToDatabase(context: Context, user: User) {
        withContext(Dispatchers.IO) {
            val userDao = DatabaseProvider.getDatabase(context).userDao()
            userDao.insertUser(user)
        }
    }

    /**
     * Id negativo fijo, con la misma convención que las notas locales: los
     * ids del servidor son positivos. Derivarlo de currentTimeMillis daba una
     * clave primaria distinta en cada registro y sin significado alguno.
     */
    private fun buildLocalUser(nombre: String, email: String) = User(
        id = ID_USUARIO_LOCAL,
        username = nombre,
        email = email,
        token = "",
        expirationDate = Long.MAX_VALUE
    )

    private const val ID_USUARIO_LOCAL = -1
}

