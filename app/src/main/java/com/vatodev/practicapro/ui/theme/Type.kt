package com.vatodev.practicapro.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.R

/** Etiquetas, epígrafes y secciones. Siempre en mayúsculas y con tracking. */
val Condensada = FontFamily(
    Font(R.font.barlow_condensed_semibold, FontWeight.SemiBold),
    Font(R.font.barlow_condensed_bold, FontWeight.Bold)
)

/** Texto corrido: contenido didáctico y enunciados de quiz. */
val Plex = FontFamily(
    Font(R.font.ibm_plex_sans_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_sans_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_sans_semibold, FontWeight.SemiBold)
)

/** Cifras: puntuaciones, IMC, PAM, temporizadores. Ancho de dígito constante. */
val PlexMono = FontFamily(
    Font(R.font.ibm_plex_mono_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium)
)

/** Epígrafe de sección: "MÓDULO 01", "PROMEDIO". */
val EtiquetaTracked = TextStyle(
    fontFamily = Condensada,
    fontWeight = FontWeight.SemiBold,
    fontSize = 13.sp,
    letterSpacing = 1.8.sp
)

/** Lectura numérica grande de calculadoras y resultados. */
val Lectura = TextStyle(
    fontFamily = PlexMono,
    fontWeight = FontWeight.Medium,
    fontSize = 44.sp,
    letterSpacing = (-1.5).sp,
    lineHeight = 46.sp
)

/** Cifras en línea: contadores, ejes de escala, metadatos. */
val Dato = TextStyle(
    fontFamily = PlexMono,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Condensada,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        letterSpacing = 3.4.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Plex,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Plex,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Plex,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Plex,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 23.sp
    ),
    labelLarge = EtiquetaTracked,
    labelMedium = EtiquetaTracked.copy(fontSize = 12.sp, letterSpacing = 1.5.sp),
    labelSmall = Dato.copy(fontSize = 11.sp)
)
