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
import com.example.practicapro.components.ActionButton
import com.example.practicapro.components.SectionContent
import com.example.practicapro.components.SectionTitle
import com.example.practicapro.components.TechniqueCard
import com.example.practicapro.components.VideoPlayerScreen


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

