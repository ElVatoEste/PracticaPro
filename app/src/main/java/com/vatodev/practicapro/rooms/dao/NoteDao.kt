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

    /** Las notas locales usan ids negativos; los del servidor son positivos. */
    @Query("SELECT MIN(id) FROM note")
    suspend fun minId(): Int?

    @Query("SELECT COUNT(*) FROM note WHERE subjectId = :subjectId")
    suspend fun countBySubject(subjectId: Int): Int

    /** Notas que el servidor todavía no ha confirmado. */
    @Query("SELECT * FROM note WHERE synced = 0")
    suspend fun getUnsynced(): List<Note>

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

