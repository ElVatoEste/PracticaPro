package com.vatodev.practicapro.ui.study.asepsia

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
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.VideoPlayerScreen
import com.vatodev.practicapro.components.module.FilaTecnica
import com.vatodev.practicapro.components.module.PantallaModulo
import com.vatodev.practicapro.components.module.PantallaPasos
import com.vatodev.practicapro.components.module.SeccionModulo
import com.vatodev.practicapro.model.MODULOS
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.repository.ContenidoRepository
import com.vatodev.practicapro.repository.SesionRepository
import com.vatodev.practicapro.repository.Tecnica
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider

private val MODULO = MODULOS.first { it.subjectId == 1 }

/** Imagen y sinopsis de cada técnica; el título y los pasos vienen del JSON. */
private val PRESENTACION = mapOf(
    "stepsLavadoClinico" to (R.drawable.ic_asepsia3 to "Elimina la flora transitoria antes y después del contacto con el paciente."),
    "stepsLavadoQuirurgico" to (R.drawable.ic_asepsia2 to "Reduce al máximo la flora residente antes de un procedimiento invasivo."),
    "stepsUsoGuantes" to (R.drawable.ic_asepsia4 to "Colocación estéril sin contaminar la superficie externa.")
)

@Composable
fun AsepsiaScreen(navController: NavController) {
    val context = LocalContext.current
    var intentos by remember { mutableIntStateOf(0) }
    var tecnicas by remember { mutableStateOf(emptyList<Tecnica>()) }
    var abierta by remember { mutableStateOf<Tecnica?>(null) }

    LaunchedEffect(navController.currentBackStackEntry) {
        intentos = DatabaseProvider.getDatabase(context).noteDao().countBySubject(MODULO.subjectId, SesionRepository.idParaConsultas(context))
        tecnicas = ContenidoRepository.tecnicas(context, MODULO.claveContenido)
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
