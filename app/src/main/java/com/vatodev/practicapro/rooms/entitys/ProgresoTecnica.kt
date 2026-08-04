package com.vatodev.practicapro.rooms.entitys

import androidx.room.Entity

/**
 * Dónde se quedó el usuario dentro de una técnica.
 *
 * La clave es la misma que identifica la técnica en
 * `assets/procedimientos.json`, así que reordenar el contenido no invalida el
 * progreso guardado.
 */
@Entity(tableName = "progreso_tecnica", primaryKeys = ["clave", "userId"])
data class ProgresoTecnica(
    val clave: String,
    /** Cuenta local a la que pertenece el progreso. */
    val userId: Int,
    val modulo: String,
    val titulo: String,
    val pasoActual: Int,
    val totalPasos: Int,
    val actualizado: Long
) {
    val completada: Boolean get() = pasoActual >= totalPasos - 1
    val fraccion: Float get() = if (totalPasos <= 1) 1f else pasoActual / (totalPasos - 1f)
}
