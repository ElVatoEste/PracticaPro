package com.example.practicapro.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
fun VideoPlayerScreen() {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    if (isPreview) {
        // Mostrar un placeholder durante la preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Vista Previa del Video")
        }
    } else {
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.Builder()
                    .setUri("android.resource://${context.packageName}/raw/tutorial_video")
                    .build()
                setMediaItem(mediaItem)
                prepare()
                pause()
            }
        }

        DisposableEffect(
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
        ) {
            onDispose {
                exoPlayer.release()
            }
        }
    }
}