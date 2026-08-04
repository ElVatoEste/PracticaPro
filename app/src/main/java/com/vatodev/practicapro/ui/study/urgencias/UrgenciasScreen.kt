package com.vatodev.practicapro.ui.study.urgencias

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.module.FilaTecnica
import com.vatodev.practicapro.components.module.PantallaModulo
import com.vatodev.practicapro.components.module.PantallaPasos
import com.vatodev.practicapro.components.module.SeccionModulo
import com.vatodev.practicapro.model.MODULOS
import com.vatodev.practicapro.repository.ContenidoRepository
import com.vatodev.practicapro.repository.Tecnica

private val MODULO = MODULOS.first { it.subjectId == 4 }

/** Imagen y sinopsis de cada técnica; el título y los pasos vienen del JSON. */
private val PRESENTACION = mapOf(
    "stepsGlucosa" to (R.drawable.ic_emergency2 to "Punción lateral en la yema, descartando la primera gota."),
    "stepsFoleyMas" to (R.drawable.ic_emergency3 to "Pene en ángulo de 90°, lubricación abundante del meato."),
    "stepsFoleyFem" to (R.drawable.ic_emergency4 to "Identificación del meato entre los labios menores."),
    "stepsSonda" to (R.drawable.ic_emergency5 to "Medición nariz-oreja-apéndice xifoides antes de introducir."),
    "stepsNaso" to (R.drawable.ic_emergency6 to "Retirada en espiración, de forma continua y rápida.")
)

@Composable
fun UrgenciasScreen() {
    val context = LocalContext.current
    var tecnicas by remember { mutableStateOf(emptyList<Tecnica>()) }
    var abierta by remember { mutableStateOf<Tecnica?>(null) }

    LaunchedEffect(Unit) {
        tecnicas = ContenidoRepository.tecnicas(context, MODULO.claveContenido)
    }

    PantallaModulo(
        indice = MODULO.indice,
        titulo = "Atención de urgencias",
        entradilla = "Estabilizar primero, diagnosticar después. Los protocolos existen para que la prisa no decida por ti.",
        imagen = R.drawable.ic_emergency1
    ) {
        SeccionModulo("Conceptos clave") {
            Text(
                text = "Una urgencia exige atención inmediata para evitar daño mayor. El protocolo sostiene al paciente mientras se prepara la intervención avanzada.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SeccionModulo("Procedimientos clave") {
            tecnicas.forEachIndexed { indice, tecnica ->
                val (imagen, sinopsis) = PRESENTACION[tecnica.clave] ?: return@forEachIndexed
                FilaTecnica(
                    numero = "%02d".format(indice + 1),
                    titulo = tecnica.titulo,
                    descripcion = sinopsis,
                    imagen = imagen,
                    onClick = { abierta = tecnica }
                )
            }
            Filete()
        }
    }

    abierta?.let { tecnica ->
        PantallaPasos(
            tecnica = tecnica,
            modulo = MODULO.claveContenido,
            onDismiss = { abierta = null }
        )
    }
}
