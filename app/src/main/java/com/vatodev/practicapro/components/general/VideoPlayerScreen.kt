package com.vatodev.practicapro.components.general

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.vatodev.practicapro.ui.theme.LocalEstado

/**
 * Reproductor de vídeo.
 *
 * El fondo del `PlayerView` se fija al de la app: por defecto es blanco y
 * asomaba como bandas alrededor del vídeo. El modo de redimensionado es FIT,
 * no ZOOM, para no recortar contenido didáctico cuando la relación de aspecto
 * del vídeo no coincide con la del contenedor.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    videoUri: String? = null,
    autoPlay: Boolean = false,
    videoAspectRatio: Float = 16f / 9f,
    modifier: Modifier = Modifier,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT
) {
    val contexto = LocalContext.current
    val fondo = MaterialTheme.colorScheme.background
    val filete = LocalEstado.current.filete

    val contenedor = modifier
        .fillMaxWidth()
        .aspectRatio(videoAspectRatio)
        .background(fondo)

    if (LocalInspectionMode.current) {
        Box(contenedor, contentAlignment = Alignment.Center) {
            Text("Vídeo", color = filete)
        }
        return
    }

    val uri = videoUri ?: "android.resource://${contexto.packageName}/raw/lavado_clinico"

    val reproductor = remember(contexto) {
        ExoPlayer.Builder(contexto).build().apply { playWhenReady = autoPlay }
    }

    LaunchedEffect(uri, autoPlay) {
        reproductor.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
        reproductor.prepare()
        reproductor.playWhenReady = autoPlay
    }

    // Sin esto el audio sigue sonando al salir de la pantalla o al bloquear.
    val ciclo = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(ciclo) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_PAUSE) reproductor.pause()
        }
        ciclo.addObserver(observador)
        onDispose { ciclo.removeObserver(observador) }
    }

    DisposableEffect(reproductor) {
        onDispose { reproductor.release() }
    }

    Box(contenedor) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = reproductor
                    useController = true
                    controllerAutoShow = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    setBackgroundColor(fondo.toArgb())
                    setShutterBackgroundColor(fondo.toArgb())
                    this.resizeMode = resizeMode
                }
            },
            update = { it.player = reproductor },
            modifier = Modifier.fillMaxSize()
        )
    }
}
