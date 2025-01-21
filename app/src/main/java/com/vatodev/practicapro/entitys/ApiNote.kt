package com.vatodev.practicapro.entitys

import com.google.gson.annotations.SerializedName

data class ApiNote(
    @SerializedName("id")
    val id: Int,

    @SerializedName("puntaje")
    val score: Int,

    @SerializedName("intento")
    val attempt: Int,

    @SerializedName("fecha")
    val date: String,

    @SerializedName("id_materia")
    val subjectId: Int, // ID de la materia

    @SerializedName("nombre_materia")
    val subjectName: String, // Nombre de la materia

    @SerializedName("nombre_usuario")
    val userName: String?, // Nombre del usuario

    @SerializedName("email_usuario")
    val userEmail: String? // Email del usuario
)
