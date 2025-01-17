package com.vatodev.practicapro.model

import com.google.gson.annotations.SerializedName

data class ConfirmationResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("statusCode")
    val statusCode: Int?,

    @SerializedName("success")
    val success: Boolean
)