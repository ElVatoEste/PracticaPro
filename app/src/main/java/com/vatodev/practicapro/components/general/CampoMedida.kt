package com.vatodev.practicapro.components.general

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado

/**
 * Entrada de una medida clínica.
 *
 * El valor se escribe donde se lee, en cifra grande y monoespaciada, en vez de
 * en un campo de formulario aparte. La unidad queda fija al lado.
 */
@Composable
fun CampoMedida(
    etiqueta: String,
    valor: String,
    unidad: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    marcador: String = "0",
    imeAction: ImeAction = ImeAction.Next,
    /** Con más de una, la unidad se vuelve un conmutador. */
    unidades: List<String> = emptyList(),
    onUnidadChange: (String) -> Unit = {}
) {
    val estado = LocalEstado.current
    val enfocado = valor.isNotEmpty()

    Column(
        modifier = modifier
            .background(estado.elevado)
            .border(1.dp, if (enfocado) estado.progreso else estado.filete, RectangleShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = etiqueta.uppercase(),
            style = EtiquetaTracked.copy(fontSize = 12.sp, letterSpacing = 1.5.sp),
            color = estado.textoSuave
        )

        Row(verticalAlignment = Alignment.Bottom) {
            val seleccion = TextSelectionColors(
                handleColor = estado.progreso,
                backgroundColor = estado.progreso.copy(alpha = 0.3f)
            )
            CompositionLocalProvider(LocalTextSelectionColors provides seleccion) {
                BasicTextField(
                    value = valor,
                    onValueChange = { onChange(it.filtrarDecimal()) },
                    singleLine = true,
                    textStyle = Dato.copy(
                        fontSize = 30.sp,
                        letterSpacing = (-1).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(estado.progreso),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = imeAction
                    ),
                    modifier = Modifier.weight(1f, fill = false),
                    decorationBox = { campo ->
                        if (valor.isEmpty()) {
                            Text(
                                text = marcador,
                                style = Dato.copy(fontSize = 30.sp, letterSpacing = (-1).sp),
                                color = estado.filete
                            )
                        }
                        campo()
                    }
                )
            }
            Spacer(Modifier.size(6.dp))
            if (unidades.size > 1) {
                val siguiente = unidades[(unidades.indexOf(unidad) + 1) % unidades.size]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                        .background(estado.progreso.copy(alpha = 0.16f))
                        .clickable { onUnidadChange(siguiente) }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = unidad,
                        style = Dato.copy(fontSize = 12.sp),
                        color = estado.progreso
                    )
                    Spacer(Modifier.size(4.dp))
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = "Cambiar a $siguiente",
                        tint = estado.progreso,
                        modifier = Modifier.size(13.dp)
                    )
                }
            } else {
                Text(
                    text = unidad,
                    style = Dato.copy(fontSize = 12.sp),
                    color = estado.textoSuave,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}
