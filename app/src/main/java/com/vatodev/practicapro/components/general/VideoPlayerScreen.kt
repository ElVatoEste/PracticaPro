package com.vatodev.practicapro.components.general

import android.net.Uri
import androidx.annotation.OptIn
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
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    videoUri: String? = null,
    autoPlay: Boolean = false,
    videoAspectRatio: Float = 16f / 9f, // Usa 1f para videos cuadrados, 16/9 para wides, etc.
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(videoAspectRatio),
    // Permite elegir el modo de redimensionado del video
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    // Vista previa para el editor de layouts
    if (isPreview) {
        Box(
            modifier = modifier.height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Vista Previa del Video")
        }
        return
    }

    // Crear y recordar el ExoPlayer, configurándolo con audio y media item
    val exoPlayer = remember(videoUri, context) {
        ExoPlayer.Builder(context).build().apply {
            val uri = videoUri ?: "android.resource://${context.packageName}/raw/lavado_clinico"
            setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
            prepare()
            playWhenReady = autoPlay
        }
    }

    // Actualiza el media item y playWhenReady si cambian los parámetros
    LaunchedEffect(videoUri, autoPlay) {
        val uri = videoUri ?: "android.resource://${context.packageName}/raw/lavado_clinico"
        exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = autoPlay
    }

    // Renderiza el PlayerView usando AndroidView
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                this.resizeMode = resizeMode
            }
        },
        modifier = modifier,
        update = { playerView ->
            playerView.player = exoPlayer
        }
    )

    // Libera los recursos del ExoPlayer al desechar el Composable
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }
}
