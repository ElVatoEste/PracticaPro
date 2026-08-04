package com.vatodev.practicapro.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationResponse(
    @SerialName("message")
    val message: String,

    @SerialName("statusCode")
    val statusCode: Int?,

    @SerialName("success")
    val success: Boolean
)