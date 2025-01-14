package com.example.practicapro.model

data class MessageResponse(
    val statusCode: Int?,
    val message: String,
    val error: String?,
)
