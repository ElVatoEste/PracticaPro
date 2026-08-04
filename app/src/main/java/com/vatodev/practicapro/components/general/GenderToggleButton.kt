package com.vatodev.practicapro.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado

@Composable
fun GenderToggleButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val estado = LocalEstado.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(110.dp)
            .height(46.dp)
            .background(if (selected) estado.progreso else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (selected) estado.progreso else estado.filete, RectangleShape)
            .toggleable(value = selected, onValueChange = { onClick() })
    ) {
        Text(
            text = label.uppercase(),
            style = EtiquetaTracked.copy(fontSize = 14.sp),
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
