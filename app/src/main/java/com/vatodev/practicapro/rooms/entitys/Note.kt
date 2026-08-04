package com.vatodev.practicapro.rooms.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    /** Negativo si la nota se creó en local; positivo si vino del servidor. */
    @PrimaryKey val id: Int,
    /** Id en el servidor. `null` mientras la nota solo exista en local. */
    val remoteId: Int? = null,
    /** `false` hasta que el servidor confirma la nota. */
    @ColumnInfo(defaultValue = "0") val synced: Boolean = false,
    val score: Int,
    val attempt: Int,
    @ColumnInfo(defaultValue = "0") val dateMillis: Long = 0L,
    val subjectId: Int,
    val subjectName: String
)
