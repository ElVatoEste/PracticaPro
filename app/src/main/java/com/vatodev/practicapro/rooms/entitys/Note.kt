package com.vatodev.practicapro.rooms.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    @PrimaryKey val id: Int,
    val score: Int,
    val attempt: Int,
    val date: String,
    val subjectId: Int,
    val subjectName: String
)
