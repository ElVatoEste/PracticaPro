package com.vatodev.practicapro.entitys

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiNote(
    @SerialName("id")
    val id: Int,

    @SerialName("puntaje")
    val score: Int,

    @SerialName("intento")
    val attempt: Int,

    @SerialName("fecha")
    val date: String,

    @SerialName("id_materia")
    val subjectId: Int, // ID de la materia

    @SerialName("nombre_materia")
    val subjectName: String, // Nombre de la materia

    @SerialName("nombre_usuario")
    val userName: String?, // Nombre del usuario

    @SerialName("email_usuario")
    val userEmail: String? // Email del usuario
)
