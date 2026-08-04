package com.vatodev.practicapro.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String
)
