package com.vatodev.practicapro.model

import com.vatodev.practicapro.repository.Contenido
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * El contenido didáctico vive en `assets/procedimientos.json`. Al ser datos y
 * no código, nada lo valida en compilación: estas pruebas lo cubren.
 *
 * Se lee del disco en lugar de los assets porque es una prueba de JVM.
 */
class ContenidoTest {

    private val contenido: Contenido = Json { ignoreUnknownKeys = true }
        .decodeFromString(File("src/main/assets/procedimientos.json").readText())

    private val tecnicas = contenido.modulos.values.flatten()

    @Test
    fun `el archivo declara los cuatro modulos`() {
        assertEquals(
            setOf("asepsia", "medicamentos", "procedimientos", "urgencias"),
            contenido.modulos.keys
        )
    }

    @Test
    fun `ninguna tecnica se queda sin pasos`() {
        tecnicas.forEach {
            assertTrue("${it.clave} no tiene pasos", it.pasos.isNotEmpty())
        }
    }

    @Test
    fun `ninguna tecnica se queda sin titulo`() {
        tecnicas.forEach {
            assertTrue("${it.clave} no tiene título", it.titulo.isNotBlank())
        }
    }

    @Test
    fun `las claves no se repiten entre modulos`() {
        val claves = tecnicas.map { it.clave }

        assertEquals(claves.size, claves.distinct().size)
    }

    /**
     * La numeración la genera la interfaz por índice. Si además viniera escrita
     * en el texto, se vería duplicada.
     */
    @Test
    fun `los pasos no llevan numeracion escrita a mano`() {
        val numerado = Regex("""^\d+[.)]\s""")

        tecnicas.forEach { tecnica ->
            tecnica.pasos.forEach { paso ->
                assertFalse("${tecnica.clave}: \"$paso\"", numerado.containsMatchIn(paso))
            }
        }
    }

    @Test
    fun `ningun paso esta vacio ni tiene espacios sobrantes`() {
        tecnicas.forEach { tecnica ->
            tecnica.pasos.forEach { paso ->
                assertTrue("${tecnica.clave} tiene un paso vacío", paso.isNotBlank())
                assertEquals("${tecnica.clave}: \"$paso\"", paso.trim(), paso)
            }
        }
    }

    @Test
    fun `conserva las diecisiete tecnicas migradas desde los Steps kt`() {
        assertEquals(17, tecnicas.size)
        assertEquals(212, tecnicas.sumOf { it.pasos.size })
    }
}
