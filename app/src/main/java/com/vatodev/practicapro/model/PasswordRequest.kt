package com.vatodev.practicapro.model

import com.google.gson.annotations.SerializedName

data class PasswordRequest(
    @SerializedName("oldPassword")
    val currentPassword: String,
    @SerializedName("newPassword")
    val newPassword: String
)
