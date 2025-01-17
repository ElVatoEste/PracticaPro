package com.vatodev.practicapro.entitys

import com.google.gson.annotations.SerializedName

data class ApiNote(
    @SerializedName("idNotas")
    val id: Int? = null,

    @SerializedName("puntaje")
    val score: Int,

    @SerializedName("intento")
    val attempt: Int? = null,

    @SerializedName("fecha")
    val date: String? = null,

    @SerializedName("materia")
    val subject: Subject
)

