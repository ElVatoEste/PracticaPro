package com.vatodev.practicapro.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.vatodev.practicapro.model.UserProfileResponse
import com.vatodev.practicapro.network.ApiClient
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {

    private val token = mutableStateOf<String?>(null)

    val userProfile = mutableStateOf<UserProfileResponse?>(null)

    private fun updateToken(newToken: String) {
        token.value = newToken
        ApiClient.setToken(newToken)
    }

    fun clearUserProfile() {
        token.value = null
        userProfile.value = null
        ApiClient.setToken(null)
    }

    suspend fun loadUserProfileFromRoom(context: Context) {
        val userDao = DatabaseProvider.getDatabase(context).userDao()
        val userEntity = withContext(Dispatchers.IO) {
            userDao.getUser()
        }
        userEntity?.let { user ->
            // Actualizamos el token
            updateToken(user.token)
            // Mapeamos la entidad a tu modelo de perfil (asegúrate de que los nombres de campos sean correctos)
            userProfile.value = UserProfileResponse(
                id = user.id,
                nombre = user.username,
                email = user.email
            )
        }
    }
    suspend fun loadTokenFromRoom(context: Context) {
            val userDao = DatabaseProvider.getDatabase(context).userDao()
            val user = withContext(Dispatchers.IO) {
                userDao.getUser()
            }
            user?.token?.let { updateToken(it) }
        }
}
