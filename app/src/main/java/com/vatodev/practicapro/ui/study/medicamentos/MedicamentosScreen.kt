package com.vatodev.practicapro.ui.study.medicamentos

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

private val MODULO = MODULOS.first { it.subjectId == 3 }

private val VIAS = listOf(
    Via("Intradérmica", "Ángulo de 15°, sin aspirar. Forma pápula visible.", R.drawable.ic_medicines2, stepsDermica),
    Via("Subcutánea", "Ángulo de 45° a 90° según el pliegue del paciente.", R.drawable.ic_medicines3, stepsSubcutaneas),
    Via("Intramuscular", "Ángulo de 90° en el sitio elegido según volumen.", R.drawable.ic_medicines4, stepsMuscular),
    Via("Intravenosa", "Canalización y verificación de retorno venoso.", R.drawable.ic_medicines5, stepsVenosa)
)

private data class Via(
    val titulo: String,
    val descripcion: String,
    val imagen: Int,
    val pasos: List<String>
)

@Composable
fun MedicamentosScreen() {
    var dialogo by remember { mutableStateOf(DialogState(false, "", emptyList())) }

    PantallaModulo(
        indice = MODULO.indice,
        titulo = "Administración de medicamentos",
        entradilla = "Medicamento correcto, dosis correcta, vía correcta, paciente correcto, hora correcta. Los cinco se verifican, no se recuerdan.",
        imagen = R.drawable.ic_medicines1
    ) {
        SeccionModulo("Conceptos clave") {
            Text(
                text = "La vía determina la velocidad de absorción y el margen de corrección. " +
                    "Una vez administrado por vía intravenosa, no hay vuelta atrás.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SeccionModulo("Vías de administración") {
            VIAS.forEachIndexed { indice, via ->
                FilaTecnica(
                    numero = "0${indice + 1}",
                    titulo = via.titulo,
                    descripcion = via.descripcion,
                    imagen = via.imagen,
                    onClick = { dialogo = DialogState(true, via.titulo, via.pasos) }
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
