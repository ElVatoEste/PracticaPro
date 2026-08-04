package com.vatodev.practicapro.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val message: String,
    val user: UserData,
    val accessToken: String,
    val expiresIn: Int
)