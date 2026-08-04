package com.vatodev.practicapro.ui.study.medicamentos

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
import com.vatodev.practicapro.components.general.MultiStepDialog
import com.vatodev.practicapro.components.module.FilaTecnica
import com.vatodev.practicapro.components.module.PantallaModulo
import com.vatodev.practicapro.components.module.SeccionModulo
import com.vatodev.practicapro.model.MODULOS
import com.vatodev.practicapro.repository.ContenidoRepository
import com.vatodev.practicapro.repository.Tecnica
import com.vatodev.practicapro.viewmodel.helper.DialogState

private val MODULO = MODULOS.first { it.subjectId == 3 }

/** Imagen y sinopsis de cada técnica; el título y los pasos vienen del JSON. */
private val PRESENTACION = mapOf(
    "stepsDermica" to (R.drawable.ic_medicines2 to "Ángulo de 15°, sin aspirar. Forma pápula visible."),
    "stepsSubcutaneas" to (R.drawable.ic_medicines3 to "Ángulo de 45° a 90° según el pliegue del paciente."),
    "stepsMuscular" to (R.drawable.ic_medicines4 to "Ángulo de 90° en el sitio elegido según volumen."),
    "stepsVenosa" to (R.drawable.ic_medicines5 to "Canalización y verificación de retorno venoso.")
)

@Composable
fun MedicamentosScreen() {
    val context = LocalContext.current
    var tecnicas by remember { mutableStateOf(emptyList<Tecnica>()) }
    var dialogo by remember { mutableStateOf(DialogState(false, "", emptyList())) }

    LaunchedEffect(Unit) {
        tecnicas = ContenidoRepository.tecnicas(context, "medicamentos")
    }

    PantallaModulo(
        indice = MODULO.indice,
        titulo = "Administración de medicamentos",
        entradilla = "Medicamento correcto, dosis correcta, vía correcta, paciente correcto, hora correcta. Los cinco se verifican, no se recuerdan.",
        imagen = R.drawable.ic_medicines1
    ) {
        SeccionModulo("Conceptos clave") {
            Text(
                text = "La vía determina la velocidad de absorción y el margen de corrección. Una vez administrado por vía intravenosa, no hay vuelta atrás.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SeccionModulo("Vías de administración") {
            tecnicas.forEachIndexed { indice, tecnica ->
                val (imagen, sinopsis) = PRESENTACION[tecnica.clave] ?: return@forEachIndexed
                FilaTecnica(
                    numero = "%02d".format(indice + 1),
                    titulo = tecnica.titulo,
                    descripcion = sinopsis,
                    imagen = imagen,
                    onClick = { dialogo = DialogState(true, tecnica.titulo, tecnica.pasos) }
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
