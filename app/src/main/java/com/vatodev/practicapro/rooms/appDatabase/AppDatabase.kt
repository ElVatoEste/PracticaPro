package com.vatodev.practicapro.rooms.appDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vatodev.practicapro.rooms.dao.*
import com.vatodev.practicapro.rooms.entitys.Materia
import com.vatodev.practicapro.rooms.entitys.Note
import com.vatodev.practicapro.rooms.entitys.ProgresoTecnica
import com.vatodev.practicapro.rooms.entitys.User

@Database(entities = [User::class, Note::class, Materia::class, ProgresoTecnica::class], version = 14, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun materiaDao(): MateriaDao
    abstract fun progresoTecnicaDao(): ProgresoTecnicaDao
}
