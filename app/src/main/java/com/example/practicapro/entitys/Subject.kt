package com.example.practicapro.entitys

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("idMateria")
    val id: Int,

    @SerializedName("nombre")
    val name: String
)
