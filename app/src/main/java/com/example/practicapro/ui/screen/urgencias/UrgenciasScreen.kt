package com.example.practicapro.ui.screen.urgencias

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.practicapro.R

@Composable
fun UrgenciasScreen(navController: NavController) {
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
            text = "Urgencias Médicas",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF7DBB00)
        )

        // Subtítulo
        Text(
            text = "Prepárate para actuar rápidamente en emergencias médicas, protegiendo vidas con procedimientos clave.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        // Imagen representativa
        Image(
            painter = painterResource(id = R.drawable.ic_emergency),
            contentDescription = "Urgencias Médicas",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        // Reproductor de video
        SectionTitle("Video Educativo")
        VideoPlayerScreen()

        // Sección de conceptos clave
        SectionTitle("Conceptos Clave")
        SectionContent(
            "Las urgencias médicas son situaciones en las que un paciente necesita atención inmediata para evitar daños mayores o la muerte. Los protocolos están diseñados para estabilizar al paciente mientras se prepara para intervenciones avanzadas."
        )
        SectionContent(
            "Conceptos importantes:\n" +
                    "- Evaluación inicial (ABC: Vía aérea, respiración, circulación).\n" +
                    "- Identificación de emergencias como paro cardíaco, shock y hemorragias graves.\n" +
                    "- Aplicación de protocolos básicos como RCP y el uso del DEA."
        )

        // Sección de técnicas específicas
        SectionTitle("Técnicas Clave en Urgencias")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechniqueCard(
                title = "Reanimación Cardiopulmonar (RCP)",
                description = "Pasos para realizar RCP efectiva en adultos y niños.",
                imageRes = R.drawable.ic_emergency
            )
            TechniqueCard(
                title = "Uso del DEA",
                description = "Guía para operar un Desfibrilador Externo Automático (DEA).",
                imageRes = R.drawable.ic_emergency
            )
            TechniqueCard(
                title = "Manejo de Shock",
                description = "Técnicas para estabilizar pacientes en shock.",
                imageRes = R.drawable.ic_emergency
            )
            TechniqueCard(
                title = "Control de Hemorragias",
                description = "Métodos básicos para detener hemorragias masivas.",
                imageRes = R.drawable.ic_emergency
            )
        }

        // Sección de recomendaciones generales
        SectionTitle("Recomendaciones")
        SectionContent(
            "1. Mantén la calma y sigue los protocolos establecidos.\n" +
                    "2. Prioriza las intervenciones que estabilicen al paciente.\n" +
                    "3. Comunica información clara y precisa al equipo médico."
        )
        SectionContent(
            "Recuerda: la seguridad del rescatador es esencial. Utiliza equipo de protección personal adecuado siempre que sea posible."
        )

        // Botón para evaluación
        ActionButton(
            text = "Realizar Evaluación",
            onClick = { navController.navigate("quiz_urgencias") } // Navega al quiz
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
    // Implementación similar al módulo de Asepsia
    Text("Aquí va el reproductor de video (pendiente de implementar)") // Placeholder
}
