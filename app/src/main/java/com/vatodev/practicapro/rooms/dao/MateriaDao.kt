package com.vatodev.practicapro.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vatodev.practicapro.rooms.entitys.Materia

@Dao
interface MateriaDao {

    @Query("SELECT * FROM materia WHERE id = :id LIMIT 1")
    suspend fun getMateriaById(id: Int): Materia?

    @Query("SELECT * FROM materia")
    suspend fun getAllMaterias(): List<Materia>

    @Insert
    suspend fun insertAll(materias: List<Materia>)
}
