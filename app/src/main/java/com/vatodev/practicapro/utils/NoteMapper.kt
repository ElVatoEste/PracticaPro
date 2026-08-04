package com.vatodev.practicapro.utils

import android.content.Context
import android.util.Log
import com.vatodev.practicapro.entitys.ApiNote
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.Note

suspend fun ApiNote.toRoomEntity(context: Context): Note {
    val database = DatabaseProvider.getDatabase(context)

    val materia = database.materiaDao().getMateriaById(this.subjectId)
    val materiaName = materia?.name ?: "Desconocido"

    Log.d("NoteMapper", "Mapeando nota: idMateria=${this.subjectId}, nombreMateria=$materiaName")

    return Note(
        id = this.id,
        remoteId = this.id,
        synced = true,
        score = this.score,
        attempt = this.attempt,
        dateMillis = this.date.toLongOrNull() ?: 0L,
        subjectId = this.subjectId,
        subjectName = materiaName,
    )
}

