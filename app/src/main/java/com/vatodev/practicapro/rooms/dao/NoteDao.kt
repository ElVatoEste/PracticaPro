package com.vatodev.practicapro.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<Note>)

    @Query("""
        SELECT COUNT(*) >= 2
        FROM note
        WHERE subjectId = :subjectId
    """)
    suspend fun hasReachedMaxAttempts(subjectId: Int): Boolean

    @Query("DELETE FROM Note")
    suspend fun deleteAllNotes()
}

