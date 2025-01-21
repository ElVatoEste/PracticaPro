package com.vatodev.practicapro.ui.study.urgencias

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.vatodev.practicapro.ui.study.medicamentos.stepsDermica

@Composable
fun UrgenciasScreen() {

    var showDialog by remember { mutableStateOf(false) }
    var selectedSteps by remember { mutableStateOf(emptyList<String>()) }
    var dialogTitle by remember { mutableStateOf("") }

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
            painter = painterResource(id = R.drawable.ic_emergency1),
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
                title = "Medición de Glucosa Capilar",
                description = "Conoce los pasos esenciales para medir la glucosa capilar de manera segura y efectiva.",
                imageRes = R.drawable.ic_emergency2,
                onClick = {
                    dialogTitle = "Medición de Glucosa Capilar"
                    selectedSteps = stepsGlucosa
                    showDialog = true
                }
            )
            TechniqueCard(
                title = "Colocación de Sonda Foley (Masculino)",
                description = "Guía detallada para la colocación segura y efectiva de una sonda Foley en pacientes masculinos.",
                imageRes = R.drawable.ic_emergency3,
                onClick = {
                    dialogTitle = "Colocación de Sonda Foley (Masculino)"
                    selectedSteps = stepsFoleyMas
                    showDialog = true
                }
            )
            TechniqueCard(
                title = "Colocación de Sonda Foley (Femenino)",
                description = "Aprende la técnica adecuada para colocar una sonda Foley en pacientes femeninos.",
                imageRes = R.drawable.ic_emergency4,
                onClick = {
                    dialogTitle = "Colocación de Sonda Foley (Femenino)"
                    selectedSteps = stepsFoleyFem
                    showDialog = true
                }
            )
            TechniqueCard(
                title = "Introducción de Sonda Nasogástrica",
                description = "Pasos detallados para realizar la introducción de una sonda nasogástrica de forma segura.",
                imageRes = R.drawable.ic_emergency5,
                onClick = {
                    dialogTitle = "Introducción de Sonda Nasogástrica"
                    selectedSteps = stepsSonda
                    showDialog = true
                }
            )
            TechniqueCard(
                title = "Retiro de Sonda Nasogástrica",
                description = "Guía paso a paso para retirar una sonda nasogástrica sin causar molestias al paciente.",
                imageRes = R.drawable.ic_emergency6,
                onClick = {
                    dialogTitle = "Retiro de Sonda Nasogástrica"
                    selectedSteps = stepsNaso
                    showDialog = true
                }
            )
        }

        // Espacio adicional al final para mejor desplazamiento
        Spacer(modifier = Modifier.weight(1f))
    }
    if (showDialog) {
        MultiStepDialog(
            title = dialogTitle,
            steps = selectedSteps,
            onDismiss = { showDialog = false }
        )
    }
}
