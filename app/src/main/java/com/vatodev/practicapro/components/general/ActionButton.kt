package com.vatodev.practicapro.components.general

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Delega en [BotonPrimario]. Se conserva la firma porque la usan todas las
 * pantallas de módulo.
 */
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    BotonPrimario(texto = text, onClick = onClick, habilitado = enabled, modifier = modifier)
}
