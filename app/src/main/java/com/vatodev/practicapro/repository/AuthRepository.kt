package com.vatodev.practicapro.repository

import android.content.Context
import com.vatodev.practicapro.network.ApiClient
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.User
import com.vatodev.practicapro.utils.Passwords
import com.vatodev.practicapro.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Cuentas locales.
 *
 * El dispositivo puede tener varias, cada una con su progreso y sus notas.
 * No hay servidor: el registro y el login se resuelven contra Room.
 */
object AuthRepository {

    suspend fun registrar(
        context: Context,
        nombre: String,
        email: String,
        password: String
    ): Result<User> = runCatching {
        val nombreLimpio = nombre.trim()
        val emailLimpio = email.trim().lowercase()

        require(nombreLimpio.isNotBlank()) { "Escribe tu nombre." }
        require(emailLimpio.isNotBlank()) { "Escribe un correo." }
        require(password.length >= MIN_PASSWORD) {
            "La contraseña necesita al menos $MIN_PASSWORD caracteres."
        }

        val dao = DatabaseProvider.getDatabase(context).userDao()
        withContext(Dispatchers.IO) {
            require(dao.porEmail(emailLimpio) == null) {
                "Ya existe una cuenta con ese correo en este dispositivo."
            }

            val sal = Passwords.nuevaSal()
            val usuario = User(
                id = minOf(dao.idMinimo() ?: 0, 0) - 1,
                username = nombreLimpio,
                email = emailLimpio,
                token = "",
                expirationDate = Long.MAX_VALUE,
                passwordHash = Passwords.derivar(password, sal),
                salt = sal,
                creada = System.currentTimeMillis()
            )
            dao.insertUser(usuario)
            usuario
        }.also { SesionRepository.abrir(context, it.id) }
    }

    suspend fun iniciarSesion(
        context: Context,
        email: String,
        password: String
    ): Result<User> = runCatching {
        val dao = DatabaseProvider.getDatabase(context).userDao()
        val usuario = withContext(Dispatchers.IO) { dao.porEmail(email.trim().lowercase()) }

        requireNotNull(usuario) { "No hay ninguna cuenta con ese correo en este dispositivo." }

        // Las cuentas creadas antes del login local no tienen contraseña.
        require(usuario.sinContrasena || Passwords.coincide(password, usuario.salt, usuario.passwordHash)) {
            "Contraseña incorrecta."
        }

        SesionRepository.abrir(context, usuario.id)
        usuario
    }

    /** Fija la contraseña de una cuenta heredada que aún no tenía. */
    suspend fun fijarContrasena(context: Context, userId: Int, password: String): Result<Unit> =
        runCatching {
            require(password.length >= MIN_PASSWORD) {
                "La contraseña necesita al menos $MIN_PASSWORD caracteres."
            }
            val dao = DatabaseProvider.getDatabase(context).userDao()
            withContext(Dispatchers.IO) {
                val usuario = requireNotNull(dao.porId(userId)) { "La cuenta ya no existe." }
                val sal = Passwords.nuevaSal()
                dao.insertUser(
                    usuario.copy(passwordHash = Passwords.derivar(password, sal), salt = sal)
                )
            }
        }

    suspend fun cuentas(context: Context): List<User> = withContext(Dispatchers.IO) {
        DatabaseProvider.getDatabase(context).userDao().todas()
    }

    suspend fun hayCuentas(context: Context): Boolean = withContext(Dispatchers.IO) {
        DatabaseProvider.getDatabase(context).userDao().cuantas() > 0
    }

    /**
     * Cierra la sesión sin borrar nada: la cuenta y su progreso siguen en el
     * dispositivo para volver a entrar.
     */
    suspend fun cerrarSesion(context: Context, userViewModel: UserViewModel) {
        SesionRepository.cerrar(context)
        ApiClient.setToken(null)
        userViewModel.clearUserProfile()
    }

    /** Borra la cuenta y todo lo suyo. Irreversible. */
    suspend fun eliminarCuenta(context: Context, userId: Int) {
        val db = DatabaseProvider.getDatabase(context)
        withContext(Dispatchers.IO) {
            db.noteDao().borrarDeUsuario(userId)
            db.progresoTecnicaDao().borrarDeUsuario(userId)
            db.userDao().borrar(userId)
        }
        if (SesionRepository.idActivo(context) == userId) SesionRepository.cerrar(context)
    }

    private const val MIN_PASSWORD = 6
}
