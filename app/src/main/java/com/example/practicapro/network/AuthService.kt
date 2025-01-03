package com.example.practicapro.network

import com.example.practicapro.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body credentials: RegisterRequest): AuthResponse

    @POST("auth/confirm-email")
    suspend fun confirmEmail(@Body confirmationRequest: ConfirmationRequest): ConfirmationResponse
}

