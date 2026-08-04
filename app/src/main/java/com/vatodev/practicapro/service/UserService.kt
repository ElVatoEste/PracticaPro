package com.vatodev.practicapro.service

import com.vatodev.practicapro.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {

    @GET("usuario/profile")
    suspend fun getProfile(): UserProfileResponse

    @POST("usuario/change-password")
    suspend fun changePassword(@Body passwordRequest: PasswordRequest): MessageResponse
}