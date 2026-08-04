package com.vatodev.practicapro.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Tecnica(
    val clave: String,
    val titulo: String,
    val pasos: List<String>
)

@Serializable
data class Contenido(
    val version: Int,
    val modulos: Map<String, List<Tecnica>>
)

/**
 * Contenido didáctico, leído de `assets/procedimientos.json`.
 *
 * Antes vivía como constantes de Kotlin repartidas en cuatro `Steps.kt`, así
 * que corregir una errata exigía recompilar y publicar. Como datos, además, la
 * numeración de los pasos la genera la interfaz por índice en lugar de estar
 * escrita dentro de cada cadena.
 */
object ContenidoRepository {

    private const val ARCHIVO = "procedimientos.json"
    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var cache: Contenido? = null

    suspend fun cargar(context: Context): Contenido {
        cache?.let { return it }
        return withContext(Dispatchers.IO) {
            val texto = context.assets.open(ARCHIVO).bufferedReader().use { it.readText() }
            json.decodeFromString<Contenido>(texto).also { cache = it }
        }
    }

    /** Técnicas de un módulo, en el orden del archivo. */
    suspend fun tecnicas(context: Context, modulo: String): List<Tecnica> =
        cargar(context).modulos[modulo].orEmpty()
}
