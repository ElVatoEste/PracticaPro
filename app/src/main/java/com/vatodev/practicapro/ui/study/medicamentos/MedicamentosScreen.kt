package com.vatodev.practicapro.ui.study.medicamentos

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

@Composable
fun MedicamentosScreen() {

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
            text = "Administración de Medicamentos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF7DBB00)
        )

        // Subtítulo
        Text(
            text = "Conoce las prácticas esenciales para la administración segura y efectiva de medicamentos.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        // Imagen representativa
        Image(
            painter = painterResource(id = R.drawable.ic_medicines),
            contentDescription = "Administración de Medicamentos",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        // Sección de conceptos clave
        SectionTitle("Conceptos Clave")
        SectionContent(
            "La administración de medicamentos implica garantizar que los pacientes reciban el tratamiento adecuado, en la dosis correcta y por la vía apropiada."
        )

        // Técnicas específicas
        SectionTitle("Técnicas Básicas")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechniqueCard(
                title = "Inyecciones Intradérmicas",
                description = "Conoce los pasos para administrar inyecciones intradérmicas asegurando la dosis correcta y el medicamento adecuado.",
                imageRes = R.drawable.ic_medicines,
                onClick = {
                    dialogTitle = "Inyecciones Intradérmicas"
                    selectedSteps = stepsDermica
                    showDialog = true
                }
            )
            TechniqueCard(
                title = "Inyecciones Subcutáneas",
                description = "Aprende cómo realizar inyecciones subcutáneas de forma segura y efectiva, minimizando riesgos.",
                imageRes = R.drawable.ic_medicines,
                onClick = {
                    dialogTitle = "Inyecciones Subcutáneas"
                    selectedSteps = stepsSubcutaneas
                    showDialog = true
                }
            )
            TechniqueCard(
                title = "Inyección Intramuscular",
                description = "Domina la técnica de administración intramuscular, garantizando seguridad y precisión.",
                imageRes = R.drawable.ic_medicines,
                onClick = {
                    dialogTitle = "Inyección Intramuscular"
                    selectedSteps = stepsMuscular
                    showDialog = true
                }
            )
            TechniqueCard(
                title = "Vía Intravenosa",
                description = "Paso a paso para colocar una vía intravenosa correctamente, asegurando una aplicación eficaz.",
                imageRes = R.drawable.ic_medicines,
                onClick = {
                    dialogTitle = "Vía Intravenosa"
                    selectedSteps = stepsVenosa
                    showDialog = true
                }
            )
        }
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