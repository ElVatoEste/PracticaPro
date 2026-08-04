package com.vatodev.practicapro.components.quizes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vatodev.practicapro.components.general.BotonPrimario
import com.vatodev.practicapro.components.general.Escala
import com.vatodev.practicapro.components.general.Etiqueta
import com.vatodev.practicapro.components.general.FilaDato
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.Tramo
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.ui.theme.LocalEstado

/**
 * Resultado de la evaluación. Usa la misma escala que las calculadoras: la
 * puntuación se lee situada entre tramos, no como una cifra suelta.
 */
@Composable
fun FinalSummary(score: Int, navController: NavController) {
    val estado = LocalEstado.current

    val mensaje = when {
        score >= 90 -> "Dominas el tema. El registro queda guardado."
        score >= 75 -> "Buen resultado. Repasa lo que fallaste antes del siguiente intento."
        score >= 50 -> "Aprobado justo. Vuelve a los pasos del módulo."
        else -> "Por debajo del mínimo. Repasa el módulo completo."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(56.dp))
        Etiqueta("Evaluación terminada")

        Spacer(Modifier.height(28.dp))
        Escala(
            valor = score.toFloat(),
            unidad = "pts",
            tramos = listOf(
                Tramo("Insuficiente", 50f, estado.error),
                Tramo("Suficiente", 75f, estado.logro),
                Tramo("Notable", 90f, estado.logro),
                Tramo("Excelente", 100f, estado.progreso)
            ),
            minimo = 0f,
            maximo = 100f
        )

        Spacer(Modifier.height(30.dp))
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(26.dp))
        Filete()
        FilaDato("Puntuación", "$score / 100")
        Filete()
        FilaDato(
            etiqueta = "Guardado",
            valor = "En el dispositivo",
            colorValor = estado.textoSuave
        )
        Filete()

        Spacer(Modifier.weight(1f))
        BotonPrimario(
            texto = "Volver al índice",
            onClick = {
                navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.MAIN) { inclusive = true }
                }
            }
        )
        Spacer(Modifier.height(32.dp))
    }
}
