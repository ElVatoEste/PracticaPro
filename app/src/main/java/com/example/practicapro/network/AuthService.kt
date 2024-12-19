package com.example.practicapro.network

import com.example.practicapro.model.LoginRequest
import com.example.practicapro.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): LoginResponse
}

