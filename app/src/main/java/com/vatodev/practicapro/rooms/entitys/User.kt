package com.vatodev.practicapro.rooms.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int = 0,
    val username: String,
    val email: String,
    val token: String,
    val expirationDate: Long
)
