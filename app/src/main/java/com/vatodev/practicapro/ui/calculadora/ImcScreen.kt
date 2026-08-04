package com.vatodev.practicapro.ui.calculadora

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vatodev.practicapro.components.general.BotonPrimario
import com.vatodev.practicapro.components.general.CampoNumerico
import com.vatodev.practicapro.components.general.BotonSecundario
import com.vatodev.practicapro.components.general.Escala
import com.vatodev.practicapro.components.general.Etiqueta
import com.vatodev.practicapro.components.general.filtrarDecimal
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.FilaDato
import com.vatodev.practicapro.components.general.GenderToggleButton
import com.vatodev.practicapro.components.general.Tramo
import com.vatodev.practicapro.ui.theme.LocalEstado
import com.vatodev.practicapro.viewmodel.ImcViewModel

@Composable
fun ImcScreen(
    navController: NavController,
    viewModel: ImcViewModel = viewModel()
) {
    var peso by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("Hombre") }

    val resultado by viewModel.resultado
    val estado = LocalEstado.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Etiqueta("Índice de masa corporal")

        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GenderToggleButton("Hombre", genero == "Hombre") { genero = "Hombre" }
            GenderToggleButton("Mujer", genero == "Mujer") { genero = "Mujer" }
        }

        Spacer(Modifier.height(16.dp))
        CampoNumerico(edad, { edad = it.filter(Char::isDigit) }, "Edad (años)")
        Spacer(Modifier.height(12.dp))
        CampoNumerico(peso, { peso = it.filtrarDecimal() }, "Peso (kg)")
        Spacer(Modifier.height(12.dp))
        CampoNumerico(talla, { talla = it.filtrarDecimal() }, "Talla (m)")

        Spacer(Modifier.height(22.dp))
        BotonPrimario(
            texto = "Calcular",
            habilitado = peso.toDoubleOrNull() != null &&
                talla.toDoubleOrNull() != null &&
                edad.toIntOrNull() != null,
            onClick = {
                viewModel.calcular(
                    peso = peso.toDouble(),
                    talla = talla.toDouble(),
                    genero = genero,
                    edad = edad.toInt()
                )
            }
        )

        resultado?.let { r ->
            Spacer(Modifier.height(32.dp))
            Etiqueta("Resultado")
            Spacer(Modifier.height(14.dp))

            Escala(
                valor = r.imc.toFloat(),
                unidad = "kg/m²",
                tramos = r.bandas.mapIndexed { indice, banda ->
                    Tramo(
                        etiqueta = banda.etiqueta,
                        hasta = if (banda.hasta == Double.MAX_VALUE) 45f else banda.hasta.toFloat(),
                        color = colorDeBanda(indice, r.bandas.size, estado.progreso, estado.logro, estado.error)
                    )
                },
                minimo = 14f,
                maximo = 45f
            )

            Spacer(Modifier.height(26.dp))
            Filete()
            FilaDato("Clasificación", r.clasificacion)
            Filete()
            FilaDato(
                etiqueta = "Peso ideal",
                valor = "%.1f – %.1f kg".format(r.pesoIdeal.start, r.pesoIdeal.endInclusive)
            )
            Filete()
            FilaDato(
                etiqueta = "Diferencia",
                valor = diferencia(peso.toDoubleOrNull(), r.pesoIdeal),
                colorValor = estado.textoSuave
            )
            Filete()
        }

        Spacer(Modifier.height(20.dp))
        BotonSecundario(texto = "Regresar", onClick = { navController.popBackStack() })
        Spacer(Modifier.height(28.dp))
    }
}


/** Verde la banda normal, morado las intermedias, coral los extremos altos. */
private fun colorDeBanda(
    indice: Int,
    total: Int,
    progreso: androidx.compose.ui.graphics.Color,
    logro: androidx.compose.ui.graphics.Color,
    error: androidx.compose.ui.graphics.Color
) = when {
    indice == 1 -> progreso
    indice >= total - 1 -> error
    else -> logro
}

private fun diferencia(peso: Double?, ideal: ClosedRange<Double>): String = when {
    peso == null -> "—"
    peso < ideal.start -> "%.1f kg por debajo".format(ideal.start - peso)
    peso > ideal.endInclusive -> "%.1f kg por encima".format(peso - ideal.endInclusive)
    else -> "Dentro del rango"
}
