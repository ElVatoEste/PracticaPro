package com.example.practicapro.rooms.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val attempt: Int,
    val date: String,
    val subjectId: Int,
    val subjectName: String
)
