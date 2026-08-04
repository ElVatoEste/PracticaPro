package com.vatodev.practicapro.components.general

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.vatodev.practicapro.ui.theme.LocalEstado

/**
 * Imagen teñida con el morado de marca.
 *
 * Las ilustraciones del proyecto vienen sobre fondo blanco y, sin tratar,
 * rompen el fondo oscuro. Se desaturan y se tiñen: la imagen aporta el
 * contraste, la marca aporta el color.
 */
@Composable
fun ImagenDuotono(
    @DrawableRes imagen: Int,
    modifier: Modifier = Modifier,
    descripcion: String? = null,
    tinte: Float = 0.55f,
    velo: Float = 0.25f,
    contentScale: ContentScale = ContentScale.Crop
) {
    val morado = LocalEstado.current.logro
    val fondo = MaterialTheme.colorScheme.background

    Box(modifier) {
        Image(
            painter = painterResource(imagen),
            contentDescription = descripcion,
            contentScale = contentScale,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.15f) }),
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(color = morado.copy(alpha = tinte), blendMode = BlendMode.Color)
                    drawRect(color = fondo.copy(alpha = velo))
                }
        )
    }
}

/** Degradado del contenido hacia el fondo, para textos sobre imagen a sangre. */
@Composable
fun DegradadoHaciaFondo(modifier: Modifier = Modifier) {
    val fondo = MaterialTheme.colorScheme.background
    Box(
        modifier.background(
            Brush.verticalGradient(
                0f to fondo.copy(alpha = 0.10f),
                0.55f to fondo.copy(alpha = 0.75f),
                1f to fondo
            )
        )
    )
}
