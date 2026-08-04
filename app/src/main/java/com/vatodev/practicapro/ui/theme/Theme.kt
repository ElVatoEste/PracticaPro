package com.vatodev.practicapro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colores de estado, fuera del ColorScheme de Material porque no son
 * primary/secondary intercambiables: cada uno significa una cosa.
 *
 * [progreso] avance y acierto · [logro] puntuación y racha · [error] fallo.
 */
data class ColoresDeEstado(
    val progreso: Color,
    val logro: Color,
    val error: Color,
    val filete: Color,
    val elevado: Color,
    val textoSuave: Color
)

val LocalEstado = staticCompositionLocalOf {
    ColoresDeEstado(Verde, MoradoClaro, Coral, FileteOscuro, ElevadoOscuro, TextoSuaveOscuro)
}

private val EsquemaOscuro = darkColorScheme(
    primary = Verde,
    onPrimary = FondoOscuro,
    secondary = MoradoClaro,
    onSecondary = FondoOscuro,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = FondoOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = ElevadoOscuro,
    onSurfaceVariant = TextoSuaveOscuro,
    outline = FileteOscuro,
    error = Coral,
    onError = FondoOscuro
)

private val EsquemaClaro = lightColorScheme(
    primary = VerdeTexto,
    onPrimary = Color.White,
    secondary = Morado,
    onSecondary = Color.White,
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = FondoClaro,
    onSurface = TextoClaro,
    surfaceVariant = ElevadoClaro,
    onSurfaceVariant = TextoSuaveClaro,
    outline = FileteClaro,
    error = Coral,
    onError = Color.White
)

/**
 * Sin dynamicColor: la identidad no puede depender del fondo de pantalla, y
 * los colores de estado deben significar lo mismo en todos los dispositivos.
 */
@Composable
fun PracticaproTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val estado = if (darkTheme) {
        ColoresDeEstado(Verde, MoradoClaro, Coral, FileteOscuro, ElevadoOscuro, TextoSuaveOscuro)
    } else {
        ColoresDeEstado(VerdeTexto, Morado, Coral, FileteClaro, ElevadoClaro, TextoSuaveClaro)
    }

    CompositionLocalProvider(LocalEstado provides estado) {
        MaterialTheme(
            colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
            typography = Typography,
            content = content
        )
    }
}
