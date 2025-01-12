package com.example.practicapro.rooms.appDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practicapro.rooms.dao.*
import com.example.practicapro.rooms.entitys.Score
import com.example.practicapro.rooms.entitys.User

@Database(entities = [User::class, Score::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun scoreDao(): ScoreDao
}