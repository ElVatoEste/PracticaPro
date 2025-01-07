package com.example.practicapro.ui.study.procedimientos

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.practicapro.R
import com.example.practicapro.components.*
import com.example.practicapro.navigation.Routes

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProcedimientosScreenPreview() {
    ProcedimientosScreen(navController = null)
}

@Composable
fun ProcedimientosScreen(navController: NavController?) {

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
         VideoPlayerScreen()

        // Listado de procedimientos básicos
        SectionTitle("Toma de signos vitales")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechniqueCard(
                title = "Talla",
                description = "Procedimiento para medir la estatura del paciente.",
                imageRes = R.drawable.ic_procedures, // Cambiar por una imagen representativa
                onClick = {
                    dialogTitle = "Talla"
                    selectedSteps = stepsTalla
                    showDialog = true
                }
            )

            TechniqueCard(
                title = "Peso",
                description = "Procedimiento para medir el peso corporal.",
                imageRes = R.drawable.ic_procedures,
                onClick = {
                    dialogTitle = "Peso"
                    selectedSteps = stepsPeso
                    showDialog = true
                }
            )

            TechniqueCard(
                title = "Frecuencia Cardíaca",
                description = "Aprende cómo medir la frecuencia cardíaca correctamente.",
                imageRes = R.drawable.ic_procedures,
                onClick = {
                    dialogTitle = "Frecuencia Cardíaca"
                    selectedSteps = stepsFrecuenciaCardiaca
                    showDialog = true
                }
            )

            TechniqueCard(
                title = "Frecuencia Respiratoria",
                description = "Aprende cómo medir la frecuencia respiratoria de manera adecuada.",
                imageRes = R.drawable.ic_procedures,
                onClick = {
                    dialogTitle = "Frecuencia Respiratoria"
                    selectedSteps = stepsFrecuenciaRespiratoria
                    showDialog = true
                }
            )

            TechniqueCard(
                title = "Presión Arterial",
                description = "Aprende cómo medir la presión arterial de manera correcta.",
                imageRes = R.drawable.ic_procedures,
                onClick = {
                    dialogTitle = "Presión Arterial"
                    selectedSteps = stepsPresionArterial
                    showDialog = true
                }
            )

        }

        ActionButton(
            text = "Realizar Evaluación",
            onClick = { navController?.navigate(Routes.QUIZ_PROCEDIMIENTOS) } // Navega al quiz
        )

        // Espacio adicional al final para desplazamiento cómodo
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showDialog) {
        MultiStepDialog(
            title = dialogTitle,
            steps = selectedSteps,
            onDismiss = { showDialog = false }
        )
    }
}

