package com.vatodev.practicapro.repository

import android.content.Context
import androidx.core.content.edit
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * Cuenta local activa.
 *
 * El id vive en `SharedPreferences` y no en Room: es preferencia de este
 * dispositivo, no un dato del usuario, y así cerrar sesión no toca la base.
 */
object SesionRepository {

    private const val PREFS = "app_preferences"
    private const val CLAVE_ACTIVO = "usuario_activo"
    private const val CLAVE_DECIDIDA = "sesion_decidida"
    private const val SIN_SESION = 0

    private val _usuarioActivo = MutableStateFlow<Int?>(null)
    val usuarioActivo: StateFlow<Int?> = _usuarioActivo

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun idActivo(context: Context): Int? =
        prefs(context).getInt(CLAVE_ACTIVO, SIN_SESION).takeIf { it != SIN_SESION }

    fun abrir(context: Context, userId: Int) {
        prefs(context).edit {
            putInt(CLAVE_ACTIVO, userId)
            putBoolean(CLAVE_DECIDIDA, true)
        }
        _usuarioActivo.value = userId
    }

    fun cerrar(context: Context) {
        prefs(context).edit {
            remove(CLAVE_ACTIVO)
            putBoolean(CLAVE_DECIDIDA, true)
        }
        _usuarioActivo.value = null
    }

    /**
     * Abre la sesión de la cuenta que quedó de una versión sin sesiones.
     *
     * Antes del login local no existía [CLAVE_ACTIVO]: al actualizar, la única
     * cuenta del dispositivo queda sin dueño visible y sus notas y su progreso
     * parecen perdidos. Se adopta una sola vez; a partir de ahí manda lo que
     * decida el usuario, de modo que cerrar sesión no se deshaga solo.
     *
     * @return `true` si quedó una sesión abierta.
     */
    suspend fun adoptarCuentaHeredada(context: Context): Boolean {
        val prefs = prefs(context)
        if (prefs.getBoolean(CLAVE_DECIDIDA, false)) return false

        val cuentas = withContext(Dispatchers.IO) {
            DatabaseProvider.getDatabase(context).userDao().todas()
        }
        val unica = cuentas.singleOrNull()
        if (unica == null) {
            prefs.edit { putBoolean(CLAVE_DECIDIDA, true) }
            return false
        }

        abrir(context, unica.id)
        return true
    }

    /** Cuenta activa, o `null` si no hay sesión o la cuenta ya no existe. */
    suspend fun usuario(context: Context): User? {
        val id = idActivo(context) ?: return null
        return withContext(Dispatchers.IO) {
            DatabaseProvider.getDatabase(context).userDao().porId(id)
        }
    }

    /**
     * Id de la cuenta activa para filtrar notas y progreso. Sin sesión abierta
     * devuelve un id imposible, de modo que las consultas no devuelvan datos
     * de otra cuenta en lugar de fallar.
     */
    fun idParaConsultas(context: Context): Int = idActivo(context) ?: Int.MIN_VALUE
}
