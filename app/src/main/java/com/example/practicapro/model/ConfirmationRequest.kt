package com.example.practicapro.model

import com.google.gson.annotations.SerializedName

data class ConfirmationRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("code")
    val code: String
)