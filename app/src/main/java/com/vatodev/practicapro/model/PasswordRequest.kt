package com.vatodev.practicapro.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PasswordRequest(
    @SerialName("oldPassword")
    val currentPassword: String,
    @SerialName("newPassword")
    val newPassword: String
)
