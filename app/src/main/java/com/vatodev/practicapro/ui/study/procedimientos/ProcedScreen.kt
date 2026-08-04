package com.vatodev.practicapro.ui.study.procedimientos

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.general.BotonSecundario
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.MultiStepDialog
import com.vatodev.practicapro.components.module.FilaTecnica
import com.vatodev.practicapro.components.module.PantallaModulo
import com.vatodev.practicapro.components.module.SeccionModulo
import com.vatodev.practicapro.model.MODULOS
import com.vatodev.practicapro.model.SUBJECT_PROCEDIMIENTOS_VF
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.repository.ContenidoRepository
import com.vatodev.practicapro.repository.Tecnica
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.viewmodel.helper.DialogState

private val MODULO = MODULOS.first { it.subjectId == 2 }

/** Imagen y sinopsis de cada técnica; el título y los pasos vienen del JSON. */
private val PRESENTACION = mapOf(
    "stepsTalla" to (R.drawable.ic_procedures2 to "Medición de la estatura con el paciente descalzo y erguido."),
    "stepsPeso" to (R.drawable.ic_procedures3 to "Medición del peso corporal en condiciones comparables."),
    "stepsFrecuenciaCardiaca" to (R.drawable.ic_procedures4 to "Palpación del pulso durante un minuto completo."),
    "stepsFrecuenciaRespiratoria" to (R.drawable.ic_procedures5 to "Conteo de ciclos sin que el paciente lo advierta."),
    "stepsPresionArterial" to (R.drawable.ic_procedures6 to "Registro con manguito del tamaño adecuado.")
)

@Composable
fun ProcedScreen(navController: NavController?) {
    val context = LocalContext.current
    var intentos by remember { mutableIntStateOf(0) }
    var intentosVf by remember { mutableIntStateOf(0) }
    var tecnicas by remember { mutableStateOf(emptyList<Tecnica>()) }
    var dialogo by remember { mutableStateOf(DialogState(false, "", emptyList())) }

    LaunchedEffect(Unit) {
        val dao = DatabaseProvider.getDatabase(context).noteDao()
        intentos = dao.countBySubject(MODULO.subjectId)
        intentosVf = dao.countBySubject(SUBJECT_PROCEDIMIENTOS_VF)
        tecnicas = ContenidoRepository.tecnicas(context, "procedimientos")
    }

    PantallaModulo(
        indice = MODULO.indice,
        titulo = "Procedimientos de enfermería",
        entradilla = "Los procedimientos básicos sostienen la seguridad del paciente y reducen el riesgo durante la atención.",
        imagen = R.drawable.ic_procedures1,
        intentosUsados = intentos,
        maxIntentos = MODULO.maxIntentos,
        evaluacionHabilitada = intentos < MODULO.maxIntentos,
        onEvaluar = { navController?.navigate(Routes.QUIZ_PROCEDIMIENTOS) },
        accionSecundaria = {
            BotonSecundario(
                texto = if (intentosVf < MODULO.maxIntentos) "Evaluación verdadero/falso" else "Sin intentos",
                habilitado = intentosVf < MODULO.maxIntentos,
                onClick = { navController?.navigate(Routes.QUIZ_PROC_TF) }
            )
        }
    ) {
        SeccionModulo("Por qué importan") {
            Text(
                text = "Un signo vital mal tomado desplaza toda la valoración que viene detrás. " +
                    "La técnica no es un trámite: es el dato.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SeccionModulo("Toma de signos vitales") {
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
