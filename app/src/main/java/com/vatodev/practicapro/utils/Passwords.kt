package com.vatodev.practicapro.utils

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Derivación de contraseñas para las cuentas locales.
 *
 * PBKDF2 con sal por cuenta. No es un almacén de credenciales de servidor,
 * pero guardar la contraseña en claro en una base que `adb` puede leer con la
 * app en modo depuración no es aceptable ni en local.
 */
object Passwords {

    private const val ITERACIONES = 120_000
    private const val LONGITUD_BITS = 256
    private const val ALGORITMO = "PBKDF2WithHmacSHA256"

    fun nuevaSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.aHex()
    }

    fun derivar(password: String, salHex: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salHex.deHex(),
            ITERACIONES,
            LONGITUD_BITS
        )
        return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded.aHex()
    }

    /** Comparación en tiempo constante: evita filtrar el prefijo correcto. */
    fun coincide(password: String, salHex: String, hashEsperado: String): Boolean {
        if (salHex.isEmpty() || hashEsperado.isEmpty()) return false
        return MessageDigest.isEqual(
            derivar(password, salHex).toByteArray(),
            hashEsperado.toByteArray()
        )
    }

    private fun ByteArray.aHex() = joinToString("") { "%02x".format(it) }

    private fun String.deHex() = ByteArray(length / 2) {
        substring(it * 2, it * 2 + 2).toInt(16).toByte()
    }
}
