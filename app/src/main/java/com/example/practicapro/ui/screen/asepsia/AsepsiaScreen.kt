package com.example.practicapro.ui.screen.asepsia


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.practicapro.R
import android.net.Uri
import androidx.media3.common.MediaItem


@Composable
fun AsepsiaScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Habilita el desplazamiento vertical
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "Técnicas de Asepsia y Antisepsia",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF7DBB00)
        )

        // Subtítulo
        Text(
            text = "Explora conceptos clave y técnicas fundamentales para prevenir infecciones.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        // Imagen representativa
        Image(
            painter = painterResource(id = R.drawable.ic_asepsia),
            contentDescription = "Imagen de Asepsia",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        // Reproductor de video
        SectionTitle("Video Introductorio")
        VideoPlayerScreen()

        // Sección de conceptos clave
        SectionTitle("Conceptos Básicos")
        SectionContent(
            "La asepsia incluye prácticas para prevenir la introducción de microorganismos en áreas críticas. Esto es fundamental en el entorno médico para proteger a los pacientes y al personal de salud."
        )

        // Sección de técnicas
        SectionTitle("Técnicas Básicas")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechniqueCard(
                title = "Lavado de Manos",
                description = "Realiza un correcto lavado de manos en 5 pasos.",
                imageRes = R.drawable.ic_asepsia
            )
            TechniqueCard(
                title = "Uso de Guantes",
                description = "Conoce el uso correcto del equipo de protección personal.",
                imageRes = R.drawable.ic_asepsia
            )
            TechniqueCard(
                title = "Limpieza de Superficies",
                description = "Minimiza riesgos limpiando áreas críticas.",
                imageRes = R.drawable.ic_asepsia
            )
        }

        // Botón para evaluación
        ActionButton(
            text = "Realizar Evaluación",
            onClick = { navController.navigate("quiz_screen") } // Navega al quiz
        )

        // Espacio adicional al final para mejor desplazamiento
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Composable para botones interactivos
@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DBB00))
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

// Composable para tarjetas de técnicas
@Composable
fun TechniqueCard(title: String, description: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// Composables para título y contenido
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SectionContent(content: String) {
    Text(
        text = content,
        fontSize = 16.sp,
        textAlign = TextAlign.Justify,
        modifier = Modifier.fillMaxWidth()
    )
}

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



