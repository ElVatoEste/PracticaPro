package com.vatodev.practicapro.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vatodev.practicapro.rooms.entitys.ProgresoTecnica

@Dao
interface ProgresoTecnicaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(progreso: ProgresoTecnica)

    @Query("SELECT * FROM progreso_tecnica WHERE clave = :clave AND userId = :userId")
    suspend fun porClave(clave: String, userId: Int): ProgresoTecnica?

    @Query("SELECT * FROM progreso_tecnica WHERE modulo = :modulo AND userId = :userId")
    suspend fun porModulo(modulo: String, userId: Int): List<ProgresoTecnica>

    /** La técnica más reciente sin terminar, para "continuar donde lo dejaste". */
    @Query(
        """SELECT * FROM progreso_tecnica
           WHERE pasoActual < totalPasos - 1 AND userId = :userId
           ORDER BY actualizado DESC LIMIT 1"""
    )
    suspend fun ultimaSinTerminar(userId: Int): ProgresoTecnica?

    @Query("DELETE FROM progreso_tecnica WHERE userId = :userId")
    suspend fun borrarDeUsuario(userId: Int)
}
