package com.example.practicapro.rooms.appDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practicapro.rooms.dao.*
import com.example.practicapro.rooms.entitys.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun scoreDao(): ScoreDao
}