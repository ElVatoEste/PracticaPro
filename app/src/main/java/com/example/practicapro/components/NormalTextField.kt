package com.example.practicapro.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.ImeAction

@Composable
fun NormalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label ?: {},
        modifier = modifier.onPreviewKeyEvent { event ->
            when {
                // Maneja la tecla Tab para pasar al siguiente input
                event.type == KeyEventType.KeyDown && event.key == Key.Tab && onNext != null -> {
                    onNext()
                    true
                }
                // Maneja Enter para terminar la acción
                event.type == KeyEventType.KeyDown && event.key == Key.Enter && onDone != null -> {
                    onDone()
                    true
                }
                else -> false
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = when {
                onNext != null -> ImeAction.Next
                onDone != null -> ImeAction.Done
                else -> ImeAction.Default
            }
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext?.invoke() },
            onDone = { onDone?.invoke() }
        ),
        singleLine = true // Esta es la clave para evitar saltos de línea
    )
}
