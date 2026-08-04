package com.vatodev.practicapro.model

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String
)
