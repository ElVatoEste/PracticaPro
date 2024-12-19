package com.example.practicapro.model

data class LoginResponse(
    val user: UserData,
    val accessToken: String,
    val expiresIn: Int // Tiempo en segundos
)