package com.vatodev.practicapro.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationRequest(
    @SerialName("email")
    val email: String,

    @SerialName("code")
    val code: String
)