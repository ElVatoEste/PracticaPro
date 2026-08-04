package com.vatodev.practicapro.ui.calculadora

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vatodev.practicapro.components.general.BotonPrimario
import com.vatodev.practicapro.components.general.CampoMedida
import com.vatodev.practicapro.components.general.Escala
import com.vatodev.practicapro.components.general.Etiqueta
import com.vatodev.practicapro.components.general.FilaDato
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.GenderToggleButton
import com.vatodev.practicapro.components.general.Tramo
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
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
    var unidadPeso by remember { mutableStateOf(KG) }
    var unidadTalla by remember { mutableStateOf(METROS) }

    val resultado by viewModel.resultado
    val estado = LocalEstado.current

    val pesoKg = peso.toDoubleOrNull()?.let { if (unidadPeso == LB) it / LIBRAS_POR_KG else it }
    val tallaM = talla.toDoubleOrNull()?.let { if (unidadTalla == CM) it / 100 else it }
    val completo = pesoKg != null && tallaM != null && edad.toIntOrNull() != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        CabeceraCalculadora("Índice de masa corporal") { navController.popBackStack() }

        Spacer(Modifier.height(22.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            CampoMedida(
                etiqueta = "Peso",
                valor = peso,
                unidad = unidadPeso,
                marcador = if (unidadPeso == KG) "68.0" else "150",
                unidades = listOf(KG, LB),
                onUnidadChange = { nueva ->
                    peso = convertir(peso, unidadPeso, nueva)
                    unidadPeso = nueva
                },
                onChange = { peso = it },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            CampoMedida(
                etiqueta = "Talla",
                valor = talla,
                unidad = unidadTalla,
                marcador = if (unidadTalla == METROS) "1.70" else "170",
                imeAction = ImeAction.Done,
                unidades = listOf(METROS, CM),
                onUnidadChange = { nueva ->
                    talla = convertir(talla, unidadTalla, nueva)
                    unidadTalla = nueva
                },
                onChange = { talla = it },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        Spacer(Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            CampoMedida(
                etiqueta = "Edad",
                valor = edad,
                unidad = "años",
                marcador = "30",
                imeAction = ImeAction.Done,
                onChange = { edad = it.filter(Char::isDigit) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                GenderToggleButton(
                    label = "Hombre",
                    selected = genero == "Hombre",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { genero = "Hombre" }
                GenderToggleButton(
                    label = "Mujer",
                    selected = genero == "Mujer",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { genero = "Mujer" }
            }
        }

        Spacer(Modifier.height(20.dp))
        BotonPrimario(
            texto = "Calcular",
            habilitado = completo,
            onClick = {
                viewModel.calcular(
                    peso = pesoKg!!,
                    talla = tallaM!!,
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
            FilaDato("Rango normal", rangoNormal(r.bandas))
            Filete()
            FilaDato(
                etiqueta = "Peso ideal estimado",
                valor = rangoPeso(r.pesoIdeal, unidadPeso)
            )
            Filete()
            FilaDato(
                etiqueta = "Diferencia",
                valor = diferencia(pesoKg, r.pesoIdeal, unidadPeso),
                colorValor = estado.textoSuave
            )
            Filete()
        }

        Spacer(Modifier.height(32.dp))
    }
}

/** Flecha de vuelta y título, como en el resto de pantallas internas. */
@Composable
fun CabeceraCalculadora(titulo: String, onAtras: () -> Unit) {
    val estado = LocalEstado.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(22.dp)
                .clickable(onClick = onAtras)
        )
        Spacer(Modifier.size(14.dp))
        Text(
            text = titulo.uppercase(),
            style = EtiquetaTracked.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Límites de la banda normal, que es la referencia que busca el usuario. */
private fun rangoNormal(bandas: List<com.vatodev.practicapro.viewmodel.Banda>): String {
    val indice = bandas.indexOfFirst { it.etiqueta.startsWith("Peso normal") }
    if (indice < 0) return "—"
    val desde = if (indice == 0) 0.0 else bandas[indice - 1].hasta
    return "%.1f – %.1f".format(desde, bandas[indice].hasta - 0.1)
}

/** Verde la banda normal, morado las intermedias, coral los extremos altos. */
private fun colorDeBanda(
    indice: Int,
    total: Int,
    progreso: Color,
    logro: Color,
    error: Color
) = when {
    indice == 1 -> progreso
    indice >= total - 1 -> error
    else -> logro
}

private fun diferencia(pesoKg: Double?, ideal: ClosedRange<Double>, unidad: String): String = when {
    pesoKg == null -> "—"
    pesoKg < ideal.start -> "%s por debajo".format(enUnidad(ideal.start - pesoKg, unidad))
    pesoKg > ideal.endInclusive -> "%s por encima".format(enUnidad(pesoKg - ideal.endInclusive, unidad))
    else -> "Dentro del rango"
}

private fun rangoPeso(ideal: ClosedRange<Double>, unidad: String) =
    "%s – %s".format(
        enUnidad(ideal.start, unidad, false),
        enUnidad(ideal.endInclusive, unidad)
    )

private fun enUnidad(kg: Double, unidad: String, conSufijo: Boolean = true): String {
    val valor = if (unidad == LB) kg * LIBRAS_POR_KG else kg
    return "%.1f".format(valor) + if (conSufijo) " $unidad" else ""
}

/** Mantiene la magnitud al cambiar de unidad: el número cambia, el peso no. */
private fun convertir(valor: String, desde: String, hasta: String): String {
    val numero = valor.toDoubleOrNull() ?: return valor
    val convertido = when {
        desde == KG && hasta == LB -> numero * LIBRAS_POR_KG
        desde == LB && hasta == KG -> numero / LIBRAS_POR_KG
        desde == METROS && hasta == CM -> numero * 100
        desde == CM && hasta == METROS -> numero / 100
        else -> return valor
    }
    return if (hasta == CM) "%.0f".format(convertido) else "%.1f".format(convertido)
}

private const val KG = "kg"
private const val LB = "lb"
private const val METROS = "m"
private const val CM = "cm"
private const val LIBRAS_POR_KG = 2.20462
