package com.vatodev.practicapro.components.general

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado

/** Epígrafe de sección o de dato. Siempre en mayúsculas. */
@Composable
fun Etiqueta(
    texto: String,
    modifier: Modifier = Modifier,
    color: Color = LocalEstado.current.textoSuave
) {
    Text(
        text = texto.uppercase(),
        style = EtiquetaTracked,
        color = color,
        modifier = modifier
    )
}

/** Filete de 1 px. Sustituye a las tarjetas como separador de contenido. */
@Composable
fun Filete(modifier: Modifier = Modifier, color: Color = LocalEstado.current.filete) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

/**
 * Acción principal. Rectangular y sin elevación: la jerarquía la marcan el
 * contraste y la posición, no la sombra.
 */
@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        shape = RectangleShape,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = LocalEstado.current.elevado,
            disabledContentColor = LocalEstado.current.textoSuave
        ),
        contentPadding = PaddingValues(vertical = 18.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    ) {
        Text(text = texto.uppercase(), style = EtiquetaTracked.copy(letterSpacing = 2.2.sp))
    }
}

/** Acción secundaria: mismo peso tipográfico, sin relleno. */
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = habilitado,
        shape = RectangleShape,
        border = BorderStroke(1.dp, LocalEstado.current.filete),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = LocalEstado.current.textoSuave
        ),
        contentPadding = PaddingValues(vertical = 18.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    ) {
        Text(text = texto.uppercase(), style = EtiquetaTracked.copy(letterSpacing = 2.2.sp))
    }
}

/**
 * Fila de registro: etiqueta a la izquierda, cifra alineada a la derecha.
 * Las cifras usan ancho de dígito constante para que columnas sucesivas
 * queden alineadas.
 */
@Composable
fun FilaDato(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    colorValor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Etiqueta(etiqueta)
        Spacer(Modifier.weight(1f))
        Text(text = valor, style = Dato.copy(fontSize = 15.sp), color = colorValor)
    }
}

/** Bloque de cifras del encabezado: tres columnas sobre fondo de campo. */
@Composable
fun Resumen(datos: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalEstado.current.elevado)
            .padding(16.dp)
    ) {
        datos.forEach { (clave, valor) ->
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Etiqueta(clave)
                Text(
                    text = valor,
                    style = Dato.copy(fontSize = 26.sp, letterSpacing = (-0.5).sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/** Intentos consumidos, como tramos llenos o vacíos. */
@Composable
fun Intentos(consumidos: Int, total: Int, modifier: Modifier = Modifier) {
    val estado = LocalEstado.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { indice ->
            Box(
                Modifier
                    .width(18.dp)
                    .height(3.dp)
                    .background(if (indice < consumidos) estado.progreso else estado.filete)
            )
        }
    }
}

