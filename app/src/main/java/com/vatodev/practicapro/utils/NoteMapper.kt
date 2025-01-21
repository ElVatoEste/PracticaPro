package com.vatodev.practicapro.utils

import android.content.Context
import android.util.Log
import com.vatodev.practicapro.entitys.ApiNote
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.Note
import javax.security.auth.Subject

// ✅ Convertir de ApiNote a Note (Room) con validación de subjectName
suspend fun ApiNote.toRoomEntity(context: Context): Note {
    val database = DatabaseProvider.getDatabase(context)

    // Obtener el nombre de la materia desde Room
    val materia = database.materiaDao().getMateriaById(this.subjectId)
    val materiaName = materia?.name ?: "Desconocido"

    // Log para depurar el mapeo
    Log.d("NoteMapper", "Mapeando nota: idMateria=${this.subjectId}, nombreMateria=$materiaName")

    return Note(
        id = this.id,
        score = this.score,
        attempt = this.attempt ?: 1,
        date = this.date ?: "",
        subjectId = this.subjectId,
        subjectName = materiaName,
    )
}

// ✅ Convertir de Note (Room) a ApiNote (API)
fun Note.toApiEntity(): ApiNote {
    return ApiNote(
        id = this.id,
        score = this.score,
        attempt = this.attempt,
        date = this.date,
        subjectId = this.subjectId,
        subjectName = this.subjectName,
        userName = null,
        userEmail = null
    )
}
