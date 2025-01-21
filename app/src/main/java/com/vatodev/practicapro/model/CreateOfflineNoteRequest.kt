package com.vatodev.practicapro.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateOfflineNoteRequest(
    val idUsuario: Int,
    val idMateria: Int,
    val puntaje: Int
)
