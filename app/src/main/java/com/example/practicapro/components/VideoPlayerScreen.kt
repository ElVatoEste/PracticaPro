package com.example.practicapro.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerScreen(
    videoUri: String? = null,
    autoPlay: Boolean = false, // Nuevo parámetro para controlar si el video se reproduce automáticamente
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    // Mostrar un placeholder durante la vista previa
    if (isPreview) {
        Box(
            modifier = modifier.height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Vista Previa del Video")
        }
        return
    }

    // Crear y configurar ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = videoUri ?: "android.resource://${context.packageName}/raw/lavado_clinico"
            val mediaItem = MediaItem.fromUri(Uri.parse(uri))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = autoPlay // Controla si el video se reproduce automáticamente
        }
    }

    // Renderizar el PlayerView
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = modifier
    )

    // Limpieza de recursos cuando el Composable se desecha
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }
}
