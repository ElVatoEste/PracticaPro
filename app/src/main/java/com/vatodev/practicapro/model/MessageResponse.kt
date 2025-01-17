package com.vatodev.practicapro.model

data class MessageResponse(
    val statusCode: Int?,
    val message: String,
    val error: String?,
)
