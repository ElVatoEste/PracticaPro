package com.vatodev.practicapro.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val id: Int,
    val nombre: String,
    val email: String,
)
