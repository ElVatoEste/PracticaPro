package com.example.practicapro.ui.screen.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AsepsiaScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "Técnicas de Asepsia y Antisepsia",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Subtítulo
        Text(
            text = "Aprende los principios básicos de asepsia para prevenir infecciones.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        // Sección de contenido
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Concepto de asepsia
            SectionTitle("¿Qué es la asepsia?")
            SectionContent(
                "La asepsia es el conjunto de prácticas destinadas a prevenir la introducción de microorganismos en áreas críticas como heridas, cavidades corporales y otros entornos susceptibles de infección."
            )

            // Tipos de asepsia
            SectionTitle("Tipos de Asepsia")
            SectionContent(
                "Existen dos tipos principales de asepsia:\n\n" +
                        "- Asepsia médica: Involucra técnicas para reducir microorganismos en superficies, piel y equipos.\n" +
                        "- Asepsia quirúrgica: Técnicas avanzadas utilizadas en procedimientos invasivos."
            )

            // Técnicas básicas
            SectionTitle("Técnicas Básicas")
            SectionContent(
                "Entre las principales técnicas están:\n\n" +
                        "- Lavado de manos: Fundamental para prevenir la propagación de infecciones.\n" +
                        "- Uso de guantes: Protege al paciente y al personal de salud.\n" +
                        "- Limpieza de superficies: Minimiza la presencia de patógenos."
            )
        }
    }
}

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
