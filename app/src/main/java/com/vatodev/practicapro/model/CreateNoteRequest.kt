package com.vatodev.practicapro.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val idMateria: Int,
    val puntaje: Int
)


