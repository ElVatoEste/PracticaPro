package com.vatodev.practicapro.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val statusCode: Int?,
    val message: String,
    val error: String?,
)
