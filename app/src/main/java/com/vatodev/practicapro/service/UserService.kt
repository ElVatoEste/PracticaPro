package com.vatodev.practicapro.service

import com.vatodev.practicapro.model.*
import retrofit2.http.GET

interface UserService {

    @GET("usuario/profile")
    suspend fun getProfile(): UserProfileResponse
}