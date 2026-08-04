package com.vatodev.practicapro.ui.calculadora

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.vatodev.practicapro.components.general.FilaDato
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.Tramo
import com.vatodev.practicapro.ui.theme.LocalEstado
import com.vatodev.practicapro.viewmodel.PamViewModel

@Composable
fun PamScreen(
    navController: NavController,
    viewModel: PamViewModel = viewModel()
) {
    var sistolica by remember { mutableStateOf("") }
    var diastolica by remember { mutableStateOf("") }

    val resultado by viewModel.resultado
    val estado = LocalEstado.current

    val sis = sistolica.toDoubleOrNull()
    val dia = diastolica.toDoubleOrNull()
    val ordenInvertido = sis != null && dia != null && dia >= sis

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Etiqueta("Presión arterial media")

        Spacer(Modifier.height(22.dp))
        CampoNumerico(sistolica, { sistolica = it.filter(Char::isDigit) }, "Sistólica (mmHg)")
        Spacer(Modifier.height(12.dp))
        CampoNumerico(diastolica, { diastolica = it.filter(Char::isDigit) }, "Diastólica (mmHg)")

        if (ordenInvertido) {
            Spacer(Modifier.height(10.dp))
            Etiqueta("La diastólica debe ser menor que la sistólica", color = estado.error)
        }

        Spacer(Modifier.height(22.dp))
        BotonPrimario(
            texto = "Calcular",
            habilitado = sis != null && dia != null && !ordenInvertido,
            onClick = { viewModel.calcular(sis!!, dia!!) }
        )

        resultado?.let { r ->
            Spacer(Modifier.height(32.dp))
            Etiqueta("Resultado")
            Spacer(Modifier.height(14.dp))

            Escala(
                valor = r.pam.toFloat(),
                unidad = "mmHg",
                tramos = r.bandas.map { banda ->
                    Tramo(
                        etiqueta = banda.etiqueta,
                        hasta = if (banda.hasta == Double.MAX_VALUE) 130f else banda.hasta.toFloat(),
                        color = when (banda.etiqueta) {
                            "Normal" -> estado.progreso
                            "Hipoperfusión", "Alta" -> estado.error
                            else -> estado.logro
                        }
                    )
                },
                minimo = 40f,
                maximo = 130f
            )

            Spacer(Modifier.height(26.dp))
            Filete()
            FilaDato("Clasificación", r.clasificacion)
            Filete()
            FilaDato("Presión de pulso", "%.0f mmHg".format(r.sistolica - r.diastolica))
            Filete()
            FilaDato(
                etiqueta = "Fórmula",
                valor = "(2 × %.0f + %.0f) ÷ 3".format(r.diastolica, r.sistolica),
                colorValor = estado.textoSuave
            )
            Filete()

            if (r.pam < 60) {
                Spacer(Modifier.height(16.dp))
                Etiqueta("Por debajo del umbral de perfusión de órganos", color = estado.error)
            }
        }

        Spacer(Modifier.height(20.dp))
        BotonSecundario(texto = "Regresar", onClick = { navController.popBackStack() })
        Spacer(Modifier.height(28.dp))
    }
}
