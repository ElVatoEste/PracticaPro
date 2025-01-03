package com.example.practicapro.model

import com.google.gson.annotations.SerializedName

data class LoginErrorResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("statusCode")
    val statusCode: Int? = null,

    @SerializedName("error")
    val error: String? = null,

    @SerializedName("isConfirmed")
    val isConfirmed: Boolean? = null
)
