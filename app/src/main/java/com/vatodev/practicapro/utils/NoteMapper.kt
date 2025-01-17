package com.vatodev.practicapro.utils

import android.content.Context
import android.util.Log
import com.vatodev.practicapro.entitys.ApiNote
import com.vatodev.practicapro.entitys.Subject
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.Note

// ✅ Convertir de ApiNote a Note (Room) con validación de subjectName
suspend fun ApiNote.toRoomEntity(context: Context): Note {
    val database = DatabaseProvider.getDatabase(context)

    // Obtener el nombre de la materia desde Room
    val materia = database.materiaDao().getMateriaById(this.subject.id)
    val materiaName = materia?.name ?: "Desconocido"

    // Log para depurar el mapeo
    Log.d("NoteMapper", "Mapeando nota: idMateria=${this.subject.id}, nombreMateria=$materiaName")

    return Note(
        score = this.score,
        attempt = this.attempt ?: 1,
        date = this.date ?: "",
        subjectId = this.subject.id,
        subjectName = materiaName
    )
}

// ✅ Convertir de Note (Room) a ApiNote (API)
fun Note.toApiEntity(): ApiNote {
    return ApiNote(
        id = this.id,
        score = this.score,
        attempt = this.attempt,
        date = this.date,
        subject = Subject(
            id = this.subjectId,
            name = this.subjectName ?: "Desconocido"
        )
    )
}
