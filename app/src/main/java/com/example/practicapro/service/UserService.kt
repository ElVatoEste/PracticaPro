package com.example.practicapro.service

import com.example.practicapro.model.*
import retrofit2.http.GET
import retrofit2.http.Header

interface UserService {

    @GET("usuario/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): UserProfileResponse
}