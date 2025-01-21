package com.vatodev.practicapro.rooms.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_requests")
data class PendingRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val endpoint: String,
    val payload: String,
    val method: String,
    val userId: Int,
    val timestamp: Long = System.currentTimeMillis()
)
