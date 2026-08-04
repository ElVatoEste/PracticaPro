package com.vatodev.practicapro.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ImcViewModelTest {

    private val vm = ImcViewModel()

    @Test
    fun `calcula el imc con la formula peso entre talla al cuadrado`() {
        vm.calcular(peso = 68.0, talla = 1.70, genero = "Hombre", edad = 30)

        assertEquals(23.53, vm.resultado.value!!.imc, 0.01)
    }

    @Test
    fun `clasifica a un hombre adulto segun los umbrales de la oms`() {
        val casos = mapOf(
            17.0 to "Bajo peso",
            22.0 to "Peso normal",
            27.0 to "Sobrepeso",
            32.0 to "Obesidad grado 1",
            37.0 to "Obesidad grado 2",
            45.0 to "Obesidad grado 3"
        )

        casos.forEach { (imc, esperada) ->
            vm.calcular(peso = imc, talla = 1.0, genero = "Hombre", edad = 30)
            assertEquals("IMC $imc", esperada, vm.resultado.value!!.clasificacion)
        }
    }

    @Test
    fun `los umbrales de mujer son mas bajos que los de hombre`() {
        vm.calcular(peso = 24.5, talla = 1.0, genero = "Mujer", edad = 30)
        val mujer = vm.resultado.value!!.clasificacion

        vm.calcular(peso = 24.5, talla = 1.0, genero = "Hombre", edad = 30)
        val hombre = vm.resultado.value!!.clasificacion

        assertEquals("Sobrepeso", mujer)
        assertEquals("Peso normal", hombre)
    }

    @Test
    fun `mayores de 65 usan una escala propia con solo tres bandas`() {
        vm.calcular(peso = 21.0, talla = 1.0, genero = "Hombre", edad = 70)

        assertEquals("Bajo peso", vm.resultado.value!!.clasificacion)
        assertEquals(3, vm.resultado.value!!.bandas.size)
    }

    @Test
    fun `menores de 18 usan la escala de adolescente`() {
        vm.calcular(peso = 16.5, talla = 1.0, genero = "Mujer", edad = 15)

        assertEquals("Bajo peso", vm.resultado.value!!.clasificacion)
    }

    /** La escala de la interfaz consume estas bandas; si divergen, miente. */
    @Test
    fun `la clasificacion coincide siempre con la banda que contiene al valor`() {
        listOf(15.0, 18.4, 18.6, 24.9, 25.1, 29.9, 30.1, 50.0).forEach { imc ->
            vm.calcular(peso = imc, talla = 1.0, genero = "Hombre", edad = 30)
            val r = vm.resultado.value!!
            val banda = r.bandas.first { imc < it.hasta }

            assertEquals("IMC $imc", banda.etiqueta, r.clasificacion)
        }
    }

    @Test
    fun `el peso ideal corresponde a la banda normal para esa talla`() {
        vm.calcular(peso = 68.0, talla = 1.70, genero = "Hombre", edad = 30)
        val ideal = vm.resultado.value!!.pesoIdeal

        // 18.5 y 25.0 sobre 1.70 m
        assertEquals(53.5, ideal.start, 0.1)
        assertEquals(72.3, ideal.endInclusive, 0.1)
    }

    @Test
    fun `una talla de cero no produce resultado en lugar de dividir por cero`() {
        vm.calcular(peso = 68.0, talla = 0.0, genero = "Hombre", edad = 30)

        assertNull(vm.resultado.value)
    }

    @Test
    fun `un peso negativo no produce resultado`() {
        vm.calcular(peso = -5.0, talla = 1.70, genero = "Hombre", edad = 30)

        assertNull(vm.resultado.value)
    }

    @Test
    fun `limpiar descarta el resultado anterior`() {
        vm.calcular(peso = 68.0, talla = 1.70, genero = "Hombre", edad = 30)
        assertTrue(vm.resultado.value != null)

        vm.limpiar()

        assertNull(vm.resultado.value)
    }
}
