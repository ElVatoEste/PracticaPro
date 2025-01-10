package com.example.practicapro.rooms.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score")
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,          // Relación con el usuario
    val quizName: String,     // Nombre de la prueba
    val score: Int,           // Puntaje obtenido
    val attempt: Int,         // Número de intento (1 o 2)
    val date: Long            // Fecha del intento
)
