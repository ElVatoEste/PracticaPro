package com.vatodev.practicapro.viewmodel

import com.vatodev.practicapro.components.quizes.EstadoOpcion
import com.vatodev.practicapro.ui.study.asepsia.quiz.estadoDe
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Antes de este cambio, al responder se atenuaban todas las opciones y nunca
 * se marcaba cuál era la correcta.
 */
class OpcionQuizTest {

    private val correcta = "De 2 a 5 minutos"
    private val otra = "20 segundos"

    @Test
    fun `sin responder todas las opciones son neutras`() {
        assertEquals(EstadoOpcion.NEUTRA, estadoDe(correcta, correcta, "", false))
        assertEquals(EstadoOpcion.NEUTRA, estadoDe(otra, correcta, "", false))
    }

    @Test
    fun `la opcion elegida se marca antes de revelar el resultado`() {
        assertEquals(EstadoOpcion.ELEGIDA, estadoDe(otra, correcta, otra, false))
    }

    @Test
    fun `al revelar se marca la correcta aunque no sea la elegida`() {
        assertEquals(EstadoOpcion.CORRECTA, estadoDe(correcta, correcta, otra, true))
    }

    @Test
    fun `al revelar la elegida equivocada se marca como incorrecta`() {
        assertEquals(EstadoOpcion.INCORRECTA, estadoDe(otra, correcta, otra, true))
    }

    @Test
    fun `acertar deja una sola opcion marcada como correcta`() {
        assertEquals(EstadoOpcion.CORRECTA, estadoDe(correcta, correcta, correcta, true))
        assertEquals(EstadoOpcion.NEUTRA, estadoDe(otra, correcta, correcta, true))
    }

    @Test
    fun `las opciones no elegidas ni correctas quedan neutras al revelar`() {
        val tercera = "10 minutos"

        assertEquals(EstadoOpcion.NEUTRA, estadoDe(tercera, correcta, otra, true))
    }
}
