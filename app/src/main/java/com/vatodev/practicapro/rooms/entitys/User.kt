package com.vatodev.practicapro.rooms.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cuenta local. Puede haber varias en el mismo dispositivo, cada una con su
 * propio progreso y sus propias notas.
 *
 * [id] negativo identifica una cuenta creada en local; los positivos quedan
 * reservados para las que confirme el servidor, con la misma convención de
 * signo que las notas.
 */
@Entity(
    tableName = "user",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey val id: Int,
    val username: String,
    val email: String,
    val token: String,
    val expirationDate: Long,
    /**
     * PBKDF2 del password en hexadecimal. Vacío en las cuentas heredadas de
     * versiones sin contraseña local.
     */
    @ColumnInfo(defaultValue = "") val passwordHash: String = "",
    @ColumnInfo(defaultValue = "") val salt: String = "",
    @ColumnInfo(defaultValue = "0") val creada: Long = 0L
) {
    /** Cuenta anterior al login local: entra sin contraseña hasta que fije una. */
    val sinContrasena: Boolean get() = passwordHash.isEmpty()
}
