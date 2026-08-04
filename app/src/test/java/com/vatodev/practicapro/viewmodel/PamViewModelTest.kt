package com.vatodev.practicapro.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PamViewModelTest {

    private val vm = PamViewModel()

    @Test
    fun `aplica la formula de presion arterial media`() {
        vm.calcular(sistolica = 120.0, diastolica = 80.0)

        // (2 * 80 + 120) / 3
        assertEquals(93.33, vm.resultado.value!!.pam, 0.01)
    }

    @Test
    fun `clasifica segun las bandas declaradas`() {
        val casos = mapOf(
            (80.0 to 40.0) to "Hipoperfusión",
            (95.0 to 55.0) to "Baja",
            (120.0 to 80.0) to "Normal",
            (140.0 to 90.0) to "Elevada",
            (180.0 to 110.0) to "Alta"
        )

        casos.forEach { (presiones, esperada) ->
            val (sis, dia) = presiones
            vm.calcular(sis, dia)
            assertEquals("$sis/$dia", esperada, vm.resultado.value!!.clasificacion)
        }
    }

    /** Por debajo de 60 mmHg no se garantiza la perfusión de órganos. */
    @Test
    fun `el umbral de hipoperfusion esta en 60 mmHg`() {
        vm.calcular(sistolica = 90.0, diastolica = 44.0)
        assertEquals("Hipoperfusión", vm.resultado.value!!.clasificacion)

        vm.calcular(sistolica = 90.0, diastolica = 46.0)
        assertEquals("Baja", vm.resultado.value!!.clasificacion)
    }

    @Test
    fun `una diastolica mayor que la sistolica no produce resultado`() {
        vm.calcular(sistolica = 80.0, diastolica = 120.0)

        assertNull(vm.resultado.value)
    }

    @Test
    fun `una diastolica igual a la sistolica no produce resultado`() {
        vm.calcular(sistolica = 100.0, diastolica = 100.0)

        assertNull(vm.resultado.value)
    }

    @Test
    fun `valores no positivos no producen resultado`() {
        vm.calcular(sistolica = 0.0, diastolica = 0.0)
        assertNull(vm.resultado.value)

        vm.calcular(sistolica = -120.0, diastolica = -80.0)
        assertNull(vm.resultado.value)
    }

    @Test
    fun `conserva las presiones de entrada para mostrar la formula`() {
        vm.calcular(sistolica = 130.0, diastolica = 85.0)
        val r = vm.resultado.value!!

        assertEquals(130.0, r.sistolica, 0.0)
        assertEquals(85.0, r.diastolica, 0.0)
    }

    @Test
    fun `la clasificacion coincide con la banda que contiene al valor`() {
        listOf(50.0 to 30.0, 100.0 to 60.0, 120.0 to 80.0, 200.0 to 120.0).forEach { (sis, dia) ->
            vm.calcular(sis, dia)
            val r = vm.resultado.value!!
            val banda = r.bandas.first { r.pam < it.hasta }

            assertEquals(banda.etiqueta, r.clasificacion)
        }
    }
}
