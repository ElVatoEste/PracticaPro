package com.vatodev.practicapro.model

import kotlinx.serialization.Serializable

@Serializable
data class EmailRequest(
    val email: String
)
