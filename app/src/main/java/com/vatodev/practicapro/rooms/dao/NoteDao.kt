package com.vatodev.practicapro.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vatodev.practicapro.rooms.entitys.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    suspend fun getAllNotes(): List<Note>

    @Query("SELECT * FROM note WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note?

    @Query("SELECT * FROM note WHERE subjectName = :quizName")
    suspend fun getNotes(quizName: String): List<Note>

    @Insert
    suspend fun insertNote(note: Note)

    @Insert
    suspend fun insertNotes(notes: List<Note>)
}

