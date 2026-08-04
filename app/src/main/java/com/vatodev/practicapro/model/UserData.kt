package com.vatodev.practicapro.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: Int,
    val nombre: String,
    val email: String
)