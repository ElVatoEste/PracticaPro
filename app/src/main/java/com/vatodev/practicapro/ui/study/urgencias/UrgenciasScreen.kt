package com.vatodev.practicapro.ui.study.urgencias

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
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.*

@Composable
fun UrgenciasScreen() {

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

        // Sección de conceptos clave
        SectionTitle("Conceptos Clave")
        SectionContent(
            "Las urgencias médicas son situaciones en las que un paciente necesita atención inmediata para evitar daños mayores o la muerte. Los protocolos están diseñados para estabilizar al paciente mientras se prepara para intervenciones avanzadas."
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

        // Espacio adicional al final para mejor desplazamiento
        Spacer(modifier = Modifier.weight(1f))
    }
}
