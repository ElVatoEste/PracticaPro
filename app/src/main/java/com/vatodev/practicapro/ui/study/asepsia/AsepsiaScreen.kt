package com.vatodev.practicapro.ui.study.asepsia

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.MultiStepDialog
import com.vatodev.practicapro.components.general.VideoPlayerScreen
import com.vatodev.practicapro.components.module.FilaTecnica
import com.vatodev.practicapro.components.module.PantallaModulo
import com.vatodev.practicapro.components.module.SeccionModulo
import com.vatodev.practicapro.model.MODULOS
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.viewmodel.helper.DialogState

private val MODULO = MODULOS.first { it.subjectId == 1 }

private val TECNICAS = listOf(
    Triple("Lavado de manos clínico", R.drawable.ic_asepsia3, stepsLavadoClinico) to
        "Elimina la flora transitoria antes y después del contacto con el paciente.",
    Triple("Lavado de manos quirúrgico", R.drawable.ic_asepsia2, stepsLavadoQuirurgico) to
        "Reduce al máximo la flora residente antes de un procedimiento invasivo.",
    Triple("Uso de guantes", R.drawable.ic_asepsia4, stepsUsoGuantes) to
        "Colocación estéril sin contaminar la superficie externa."
)

@Composable
fun AsepsiaScreen(navController: NavController) {
    val context = LocalContext.current
    var intentos by remember { mutableIntStateOf(0) }
    var dialogo by remember { mutableStateOf(DialogState(false, "", emptyList())) }

    LaunchedEffect(navController.currentBackStackEntry) {
        val dao = DatabaseProvider.getDatabase(context).noteDao()
        intentos = dao.countBySubject(MODULO.subjectId)
    }

    PantallaModulo(
        indice = MODULO.indice,
        titulo = "Asepsia y antisepsia",
        entradilla = "Barreras, lavado de manos y campo estéril. Tres técnicas que sostienen todo lo demás.",
        imagen = R.drawable.ic_asepcia1,
        intentosUsados = intentos,
        maxIntentos = MODULO.maxIntentos,
        evaluacionHabilitada = intentos < MODULO.maxIntentos,
        onEvaluar = { navController.navigate(Routes.QUIZ_SCREEN) }
    ) {
        SeccionModulo("Conceptos básicos") {
            Text(
                text = "La asepsia agrupa las prácticas que impiden la introducción de " +
                    "microorganismos en áreas críticas. Sostiene la seguridad del paciente y " +
                    "la del personal.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SeccionModulo("Demostración") {
            VideoPlayerScreen(
                videoAspectRatio = 16f / 9f,
                videoUri = "android.resource://${context.packageName}/${R.raw.videotutorial}"
            )
        }

        SeccionModulo("Procedimientos antisépticos") {
            TECNICAS.forEachIndexed { indice, (tecnica, descripcion) ->
                val (titulo, imagen, pasos) = tecnica
                FilaTecnica(
                    numero = "0${indice + 1}",
                    titulo = titulo,
                    descripcion = descripcion,
                    imagen = imagen,
                    onClick = { dialogo = DialogState(true, titulo, pasos) }
                )
            }
            Filete()
        }

        Spacer(Modifier.height(0.dp))
    }

    if (dialogo.showDialog) {
        MultiStepDialog(
            title = dialogo.title,
            steps = dialogo.steps,
            onDismiss = { dialogo = dialogo.copy(showDialog = false) }
        )
    }
}
