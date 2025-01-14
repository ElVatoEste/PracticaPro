package com.example.practicapro.rooms.appDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practicapro.rooms.dao.*
import com.example.practicapro.rooms.entitys.Materia
import com.example.practicapro.rooms.entitys.PendingRequest
import com.example.practicapro.rooms.entitys.Note
import com.example.practicapro.rooms.entitys.User

@Database(entities = [User::class, Note::class, PendingRequest::class, Materia::class], version = 5, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun pendingRequestDao(): PendingRequestDao
    abstract fun materiaDao(): MateriaDao
}
