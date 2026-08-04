package com.vatodev.practicapro.components.module

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vatodev.practicapro.components.general.Etiqueta
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.quizes.ProgressBar
import com.vatodev.practicapro.repository.ProgresoRepository
import com.vatodev.practicapro.repository.Tecnica
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado
import kotlinx.coroutines.launch

/**
 * Pasos de una técnica, a pantalla completa y uno por vista.
 *
 * Retoma donde se dejó: el paso actual se persiste en cada avance, así que
 * salir a mitad de un procedimiento largo no obliga a empezar de cero.
 */
@Composable
fun PantallaPasos(
    tecnica: Tecnica,
    modulo: String,
    onDismiss: () -> Unit
) {
    if (tecnica.pasos.isEmpty()) return

    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()
    val estado = LocalEstado.current

    var paso by remember(tecnica.clave) { mutableIntStateOf(0) }
    var retomado by remember(tecnica.clave) { mutableIntStateOf(-1) }

    LaunchedEffect(tecnica.clave) {
        val guardado = ProgresoRepository.paso(contexto, tecnica.clave)
        if (guardado != null && guardado in 1 until tecnica.pasos.size) {
            paso = guardado
            retomado = guardado
        }
    }

    fun ir(destino: Int) {
        paso = destino.coerceIn(0, tecnica.pasos.lastIndex)
        alcance.launch {
            ProgresoRepository.guardar(contexto, tecnica, modulo, paso)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {

                Column(Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Etiqueta("Paso ${paso + 1} de ${tecnica.pasos.size}")
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = estado.textoSuave,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(onClick = onDismiss)
                        )
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = tecnica.titulo,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(16.dp))
                    ProgressBar(currentStep = paso, totalSteps = tecnica.pasos.size)

                    if (retomado > 0 && paso == retomado) {
                        Spacer(Modifier.height(12.dp))
                        Etiqueta("Retomado donde lo dejaste", color = estado.logro)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 32.dp)
                ) {
                    AnimatedContent(
                        targetState = paso,
                        transitionSpec = {
                            val avanza = targetState > initialState
                            val despl = if (avanza) 60 else -60
                            (slideInHorizontally(tween(260)) { despl } + fadeIn(tween(260)))
                                .togetherWith(
                                    slideOutHorizontally(tween(200)) { -despl } + fadeOut(tween(200))
                                )
                                .using(SizeTransform(clip = false))
                        },
                        label = "paso"
                    ) { indice ->
                        Column {
                            Text(
                                text = "%02d".format(indice + 1),
                                style = Dato.copy(fontSize = 40.sp, letterSpacing = (-1).sp),
                                color = estado.progreso
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = tecnica.pasos[indice],
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 20.sp,
                                    lineHeight = 31.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.widthIn(max = 560.dp)
                            )
                        }
                    }
                }

                Filete()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ControlPaso(
                        texto = "Anterior",
                        icono = Icons.AutoMirrored.Filled.ArrowBack,
                        iconoAlFinal = false,
                        habilitado = paso > 0,
                        color = estado.textoSuave,
                        onClick = { ir(paso - 1) },
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        Modifier
                            .size(width = 1.dp, height = 72.dp)
                            .clickable(enabled = false) {}
                    ) { Filete() }
                    if (paso < tecnica.pasos.lastIndex) {
                        ControlPaso(
                            texto = "Siguiente",
                            icono = Icons.AutoMirrored.Filled.ArrowForward,
                            iconoAlFinal = true,
                            habilitado = true,
                            color = estado.progreso,
                            onClick = { ir(paso + 1) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        ControlPaso(
                            texto = "Terminar",
                            icono = Icons.Default.Check,
                            iconoAlFinal = true,
                            habilitado = true,
                            color = estado.progreso,
                            onClick = {
                                ir(tecnica.pasos.lastIndex)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlPaso(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    iconoAlFinal: Boolean,
    habilitado: Boolean,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tinte = if (habilitado) color else LocalEstado.current.filete

    Row(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = habilitado, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!iconoAlFinal) {
            Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
        }
        Text(text = texto.uppercase(), style = EtiquetaTracked.copy(fontSize = 13.sp), color = tinte)
        if (iconoAlFinal) {
            Spacer(Modifier.size(8.dp))
            Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(18.dp))
        }
    }
}
