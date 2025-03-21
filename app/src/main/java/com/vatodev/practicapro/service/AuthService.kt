package com.vatodev.practicapro.service

import com.vatodev.practicapro.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body credentials: RegisterRequest): RegisterResponse

    @POST("auth/confirm-email")
    suspend fun confirmEmail(@Body confirmationRequest: ConfirmationRequest): ConfirmationResponse

    @POST("auth/resend-verification")
    suspend fun resentEmail(@Body emailRequest: EmailRequest): MessageResponse

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): MessageResponse
}

