package com.example.practicapro.ui.screen.procedimientos

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.practicapro.R
import com.example.practicapro.ui.navigation.Routes

@Composable
fun ProcedimientosScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "Procedimientos Básicos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF7DBB00)
        )

        // Subtítulo
        Text(
            text = "Aprende los procedimientos fundamentales para el cuidado del paciente.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        // Imagen representativa
        Image(
            painter = painterResource(id = R.drawable.ic_procedures), // Cambiar por una imagen específica
            contentDescription = "Imagen de Procedimientos Básicos",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        // Sección de conceptos clave
        SectionTitle("Importancia de los Procedimientos Básicos")
        SectionContent(
            "Los procedimientos básicos son esenciales para garantizar la seguridad y el bienestar del paciente, así como para reducir riesgos durante la atención médica."
        )

        // Video Educativo
        SectionTitle("Video Educativo")
//        VideoPlayerScreen(
//            videoUri = "android.resource://${LocalContext.current.packageName}/raw/procedimientos_video"
//        )

        // Listado de procedimientos básicos
        SectionTitle("Listado de Procedimientos")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechniqueCard(
                title = "Toma de Signos Vitales",
                description = "Conoce cómo realizar una correcta medición de presión arterial, temperatura y pulso.",
                imageRes = R.drawable.ic_procedures // Reemplazar con imagen específica
            )
            TechniqueCard(
                title = "Colocación de Vendajes",
                description = "Aprende las técnicas básicas para colocar vendajes de manera segura.",
                imageRes = R.drawable.ic_procedures // Reemplazar con imagen específica
            )
            TechniqueCard(
                title = "Atención de Heridas",
                description = "Procedimientos para una limpieza y cuidado efectivo de heridas.",
                imageRes = R.drawable.ic_procedures // Reemplazar con imagen específica
            )
        }

// Botón para evaluación
        ActionButton(
            text = "Realizar Evaluación",
            onClick = { navController.navigate(Routes.QUIZ_PROCEDIMIENTOS) } // Navega al quiz
        )


        // Espacio adicional al final para desplazamiento cómodo
        Spacer(modifier = Modifier.height(32.dp))
    }
}

//// Reproductor de Video
//@Composable
//fun VideoPlayerScreen(videoUri: String) {
//    val context = LocalContext.current
//
//    val exoPlayer = remember {
//        ExoPlayer.Builder(context).build().apply {
//            val mediaItem = MediaItem.Builder()
//                .setUri(Uri.parse(videoUri))
//                .build()
//            setMediaItem(mediaItem)
//            prepare()
//            pause()
//        }
//    }
//
//    DisposableEffect(
//        AndroidView(
//            factory = {
//                PlayerView(context).apply {
//                    player = exoPlayer
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(16f / 9f) // Mantener proporción 16:9
//        )
//    ) {
//        onDispose {
//            exoPlayer.release()
//        }
//    }
//}

// Composable para título de sección
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

// Composable para contenido de sección
@Composable
fun SectionContent(content: String) {
    Text(
        text = content,
        fontSize = 16.sp,
        textAlign = TextAlign.Justify,
        modifier = Modifier.fillMaxWidth()
    )
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
