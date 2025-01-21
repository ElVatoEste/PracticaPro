package com.vatodev.practicapro.rooms.appDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vatodev.practicapro.rooms.dao.*
import com.vatodev.practicapro.rooms.entitys.Materia
import com.vatodev.practicapro.rooms.entitys.PendingRequest
import com.vatodev.practicapro.rooms.entitys.Note
import com.vatodev.practicapro.rooms.entitys.User

@Database(entities = [User::class, Note::class, PendingRequest::class, Materia::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun pendingRequestDao(): PendingRequestDao
    abstract fun materiaDao(): MateriaDao
}
