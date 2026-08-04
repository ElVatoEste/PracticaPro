package com.vatodev.practicapro.ui.study.procedimientos

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.viewmodel.helper.DialogState

private val MODULO = MODULOS.first { it.subjectId == 2 }

/** Segunda evaluación del módulo, en formato verdadero/falso. */
private const val SUBJECT_VERDADERO_FALSO = 5

private val SIGNOS = listOf(
    Signo("Talla", "Medición de la estatura con el paciente descalzo y erguido.", R.drawable.ic_procedures2, stepsTalla),
    Signo("Peso", "Medición del peso corporal en condiciones comparables.", R.drawable.ic_procedures3, stepsPeso),
    Signo("Frecuencia cardíaca", "Palpación del pulso durante un minuto completo.", R.drawable.ic_procedures4, stepsFrecuenciaCardiaca),
    Signo("Frecuencia respiratoria", "Conteo de ciclos sin que el paciente lo advierta.", R.drawable.ic_procedures5, stepsFrecuenciaRespiratoria),
    Signo("Presión arterial", "Registro con manguito del tamaño adecuado.", R.drawable.ic_procedures6, stepsPresionArterial)
)

private data class Signo(
    val titulo: String,
    val descripcion: String,
    val imagen: Int,
    val pasos: List<String>
)

@Composable
fun ProcedScreen(navController: NavController?) {
    val context = LocalContext.current
    var intentos by remember { mutableIntStateOf(0) }
    var intentosVf by remember { mutableIntStateOf(0) }
    var dialogo by remember { mutableStateOf(DialogState(false, "", emptyList())) }

    LaunchedEffect(Unit) {
        val dao = DatabaseProvider.getDatabase(context).noteDao()
        intentos = dao.countBySubject(MODULO.subjectId)
        intentosVf = dao.countBySubject(SUBJECT_VERDADERO_FALSO)
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
            SIGNOS.forEachIndexed { indice, signo ->
                FilaTecnica(
                    numero = "0${indice + 1}",
                    titulo = signo.titulo,
                    descripcion = signo.descripcion,
                    imagen = signo.imagen,
                    onClick = { dialogo = DialogState(true, signo.titulo, signo.pasos) }
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
