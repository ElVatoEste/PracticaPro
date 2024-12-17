package com.example.practicapro.components

import android.net.Uri
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerScreen() {
    val context = LocalContext.current

    // Configuración de ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse("android.resource://${context.packageName}/raw/tutorial_video"))
                .build()
            setMediaItem(mediaItem)
            prepare() // Prepara el video
            pause()   // Inicia en pausa
        }
    }

    // Mostrar el PlayerView de ExoPlayer
    DisposableEffect(
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier
                .fillMaxWidth() // Ocupa todo el ancho de la pantalla
                .aspectRatio(16f / 9f) // Mantiene la proporción de aspecto 16:9
        )
    ) {
        onDispose {
            exoPlayer.release() // Libera el reproductor al salir de la pantalla
        }
    }
}



