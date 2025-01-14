package com.example.practicapro.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.practicapro.rooms.entitys.Materia

@Dao
interface MateriaDao {

    @Query("SELECT * FROM materia WHERE id = :id LIMIT 1")
    suspend fun getMateriaById(id: Int): Materia?

    @Insert
    suspend fun insertAll(materias: List<Materia>)
}
