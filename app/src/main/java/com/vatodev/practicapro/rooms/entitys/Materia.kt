package com.vatodev.practicapro.rooms.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materia")
data class Materia(
    @PrimaryKey val id: Int,
    val name: String
)
