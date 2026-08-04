package com.vatodev.practicapro.components.general

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.vatodev.practicapro.ui.theme.LocalEstado

/** Entrada numérica de las calculadoras. Rectangular, sin relleno. */
@Composable
fun CampoNumerico(
    valor: String,
    onChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    val estado = LocalEstado.current
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(etiqueta, style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        shape = RectangleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = estado.progreso,
            unfocusedBorderColor = estado.filete,
            focusedLabelColor = estado.progreso,
            unfocusedLabelColor = estado.textoSuave
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/** Deja pasar dígitos y un único punto decimal. */
fun String.filtrarDecimal(): String =
    filterIndexed { i, c -> c.isDigit() || (c == '.' && !take(i).contains('.')) }
