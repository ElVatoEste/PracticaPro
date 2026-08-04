package com.vatodev.practicapro.ui.study.urgencias

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.MultiStepDialog
import com.vatodev.practicapro.components.module.FilaTecnica
import com.vatodev.practicapro.components.module.PantallaModulo
import com.vatodev.practicapro.components.module.SeccionModulo
import com.vatodev.practicapro.model.MODULOS
import com.vatodev.practicapro.viewmodel.helper.DialogState

private val MODULO = MODULOS.first { it.subjectId == 4 }

private val PROCEDIMIENTOS = listOf(
    Procedimiento("Glucosa capilar", "Punción lateral en la yema, descartando la primera gota.", R.drawable.ic_emergency2, stepsGlucosa),
    Procedimiento("Sonda Foley masculina", "Pene en ángulo de 90°, lubricación abundante del meato.", R.drawable.ic_emergency3, stepsFoleyMas),
    Procedimiento("Sonda Foley femenina", "Identificación del meato entre los labios menores.", R.drawable.ic_emergency4, stepsFoleyFem),
    Procedimiento("Sonda nasogástrica", "Medición nariz-oreja-apéndice xifoides antes de introducir.", R.drawable.ic_emergency5, stepsSonda),
    Procedimiento("Retiro de nasogástrica", "Retirada en espiración, de forma continua y rápida.", R.drawable.ic_emergency6, stepsNaso)
)

private data class Procedimiento(
    val titulo: String,
    val descripcion: String,
    val imagen: Int,
    val pasos: List<String>
)

@Composable
fun UrgenciasScreen() {
    var dialogo by remember { mutableStateOf(DialogState(false, "", emptyList())) }

    PantallaModulo(
        indice = MODULO.indice,
        titulo = "Atención de urgencias",
        entradilla = "Estabilizar primero, diagnosticar después. Los protocolos existen para que la prisa no decida por ti.",
        imagen = R.drawable.ic_emergency1
    ) {
        SeccionModulo("Conceptos clave") {
            Text(
                text = "Una urgencia exige atención inmediata para evitar daño mayor. " +
                    "El protocolo sostiene al paciente mientras se prepara la intervención avanzada.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SeccionModulo("Procedimientos clave") {
            PROCEDIMIENTOS.forEachIndexed { indice, proc ->
                FilaTecnica(
                    numero = "0${indice + 1}",
                    titulo = proc.titulo,
                    descripcion = proc.descripcion,
                    imagen = proc.imagen,
                    onClick = { dialogo = DialogState(true, proc.titulo, proc.pasos) }
                )
            }
            Filete()
        }
    }

    if (dialogo.showDialog) {
        MultiStepDialog(
            title = dialogo.title,
            steps = dialogo.steps,
            onDismiss = { dialogo = dialogo.copy(showDialog = false) }
        )
    }
}
