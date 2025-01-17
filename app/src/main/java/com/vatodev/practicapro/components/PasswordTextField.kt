package com.vatodev.practicapro.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label ?: {},
        modifier = modifier.onPreviewKeyEvent { event ->
            when {
                event.type == KeyEventType.KeyDown && event.key == Key.Tab && onNext != null -> {
                    onNext()
                    true
                }
                event.type == KeyEventType.KeyDown && event.key == Key.Enter && onDone != null -> {
                    onDone()
                    true
                }
                else -> false
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
        singleLine = true,
        trailingIcon = {
            val icon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            Icon(
                imageVector = icon,
                contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
            )
        }
    )
}
