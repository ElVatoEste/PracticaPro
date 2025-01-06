package com.example.practicapro.model

data class AuthResponse(
    val message: String,
    val user: UserData,
    val accessToken: String,
    val expiresIn: Int
)