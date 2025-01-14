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

    val token = mutableStateOf<String?>(null)

    private val userProfile = mutableStateOf<UserProfileResponse?>(null)

    fun updateToken(newToken: String) {
        token.value = newToken
        ApiClient.setToken(newToken)
    }


    fun clearUserProfile() {
        token.value = null
        userProfile.value = null
        ApiClient.setToken(null)
    }

    suspend fun loadTokenFromRoom(context: Context) {
        val userDao = DatabaseProvider.getDatabase(context).userDao()
        val user = withContext(Dispatchers.IO) {
            userDao.getUser()
        }
        user?.token?.let { updateToken(it) }
    }
}
