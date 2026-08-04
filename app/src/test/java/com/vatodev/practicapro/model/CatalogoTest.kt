package com.vatodev.practicapro.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El catálogo es la fuente única de módulos, rutas y subjectId. Antes estaba
 * duplicado entre MainScreen, las pantallas de módulo y los quizzes.
 */
class CatalogoTest {

    @Test
    fun `no hay subjectId repetidos`() {
        val ids = MODULOS.map { it.subjectId }

        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `no hay rutas repetidas`() {
        val rutas = MODULOS.map { it.ruta }

        assertEquals(rutas.size, rutas.distinct().size)
    }

    @Test
    fun `no hay indices repetidos`() {
        val indices = MODULOS.map { it.indice }

        assertEquals(indices.size, indices.distinct().size)
    }

    @Test
    fun `las materias cubren todos los modulos mas la evaluacion verdadero falso`() {
        val idsMaterias = MATERIAS.map { it.id }.toSet()

        MODULOS.forEach {
            assertTrue("falta materia para ${it.nombre}", it.subjectId in idsMaterias)
        }
        assertTrue(SUBJECT_PROCEDIMIENTOS_VF in idsMaterias)
        assertEquals(MODULOS.size + 1, MATERIAS.size)
    }

    @Test
    fun `el subjectId de verdadero falso no colisiona con ningun modulo`() {
        assertTrue(MODULOS.none { it.subjectId == SUBJECT_PROCEDIMIENTOS_VF })
    }

    @Test
    fun `todos los modulos permiten al menos un intento`() {
        MODULOS.forEach {
            assertTrue("${it.nombre} tiene ${it.maxIntentos} intentos", it.maxIntentos >= 1)
        }
    }
}
