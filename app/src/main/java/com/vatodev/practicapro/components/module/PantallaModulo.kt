package com.vatodev.practicapro.components.module

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.components.general.BotonPrimario
import com.vatodev.practicapro.components.general.Etiqueta
import com.vatodev.practicapro.components.general.DegradadoHaciaFondo
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.ImagenDuotono
import com.vatodev.practicapro.components.general.Intentos
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.LocalEstado

/**
 * Estructura común de las cuatro pantallas de módulo: imagen a sangre con
 * degradado, cuerpo desplazable y llamada a la evaluación anclada al final.
 */
@Composable
fun PantallaModulo(
    indice: String,
    titulo: String,
    entradilla: String,
    imagen: Int,
    intentosUsados: Int = 0,
    maxIntentos: Int = 0,
    evaluacionHabilitada: Boolean = false,
    onEvaluar: (() -> Unit)? = null,
    accionSecundaria: (@Composable () -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val estado = LocalEstado.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeroModulo(indice = indice, titulo = titulo, imagen = imagen)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = entradilla,
                style = MaterialTheme.typography.bodyLarge,
                color = estado.textoSuave
            )
            Spacer(Modifier.height(24.dp))
            contenido()

            if (onEvaluar != null) {
                Spacer(Modifier.height(28.dp))
                BotonPrimario(
                    texto = if (evaluacionHabilitada) "Realizar evaluación" else "Sin intentos",
                    habilitado = evaluacionHabilitada,
                    onClick = onEvaluar
                )
                accionSecundaria?.let {
                    Spacer(Modifier.height(10.dp))
                    it()
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Intentos(consumidos = intentosUsados, total = maxIntentos)
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "$intentosUsados de $maxIntentos intentos usados",
                        style = Dato.copy(fontSize = 11.sp),
                        color = estado.textoSuave
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun HeroModulo(indice: String, titulo: String, imagen: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        ImagenDuotono(
            imagen = imagen,
            tinte = 0.45f,
            velo = 0.10f,
            modifier = Modifier.fillMaxSize()
        )
        DegradadoHaciaFondo(Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Etiqueta("Módulo $indice", color = LocalEstado.current.progreso)
            Spacer(Modifier.height(10.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/** Encabezado de sección dentro del cuerpo del módulo. */
@Composable
fun SeccionModulo(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Etiqueta(titulo)
    Spacer(Modifier.height(12.dp))
    Column(content = contenido)
    Spacer(Modifier.height(26.dp))
}

/**
 * Técnica como fila reglada en lugar de tarjeta. La miniatura da contexto sin
 * competir con el título.
 */
@Composable
fun FilaTecnica(
    numero: String,
    titulo: String,
    descripcion: String,
    imagen: Int,
    onClick: () -> Unit
) {
    val estado = LocalEstado.current
    Column {
        Filete()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = numero, style = Dato.copy(fontSize = 12.sp), color = estado.progreso)
            ImagenDuotono(imagen = imagen, modifier = Modifier.size(52.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = estado.textoSuave
                )
            }
        }
    }
}
