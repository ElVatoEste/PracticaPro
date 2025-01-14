package com.example.practicapro.service

import com.example.practicapro.model.*
import retrofit2.http.GET

interface UserService {

    @GET("usuario/profile")
    suspend fun getProfile(): UserProfileResponse
}