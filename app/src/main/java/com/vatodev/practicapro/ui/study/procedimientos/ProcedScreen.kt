package com.vatodev.practicapro.ui.study.procedimientos

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.*
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider

@Composable
fun ProcedScreen(navController: NavController?) {

    val context = LocalContext.current

    // Estados para los botones
    var isButton1Enabled by remember { mutableStateOf(false) }
    var isButton2Enabled by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedSteps by remember { mutableStateOf(emptyList<String>()) }
    var dialogTitle by remember { mutableStateOf("") }

    // Validar estado de los botones cada vez que se entra a la pantalla
    LaunchedEffect(Unit) {
        val database = DatabaseProvider.getDatabase(context)
        val noteDao = database.noteDao()

        isButton1Enabled = !noteDao.hasReachedMaxAttempts(2) // Para subjectId 2
        isButton2Enabled = !noteDao.hasReachedMaxAttempts(5) // Para subjectId 5

        Log.d("ProcedScreen", "Estado de botón 1: $isButton1Enabled")
        Log.d("ProcedScreen", "Estado de botón 2: $isButton2Enabled")
    }

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
            painter = painterResource(id = R.drawable.ic_procedures1), // Cambiar por una imagen específica
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
                imageRes = R.drawable.ic_procedures2, // Cambiar por una imagen representativa
                onClick = {
                    dialogTitle = "Talla"
                    selectedSteps = stepsTalla
                    showDialog = true
                }
            )

            TechniqueCard(
                title = "Peso",
                description = "Procedimiento para medir el peso corporal.",
                imageRes = R.drawable.ic_procedures3,
                onClick = {
                    dialogTitle = "Peso"
                    selectedSteps = stepsPeso
                    showDialog = true
                }
            )

            TechniqueCard(
                title = "Frecuencia Cardíaca",
                description = "Aprende cómo medir la frecuencia cardíaca correctamente.",
                imageRes = R.drawable.ic_procedures4,
                onClick = {
                    dialogTitle = "Frecuencia Cardíaca"
                    selectedSteps = stepsFrecuenciaCardiaca
                    showDialog = true
                }
            )

            TechniqueCard(
                title = "Frecuencia Respiratoria",
                description = "Aprende cómo medir la frecuencia respiratoria de manera adecuada.",
                imageRes = R.drawable.ic_procedures5,
                onClick = {
                    dialogTitle = "Frecuencia Respiratoria"
                    selectedSteps = stepsFrecuenciaRespiratoria
                    showDialog = true
                }
            )

            TechniqueCard(
                title = "Presión Arterial",
                description = "Aprende cómo medir la presión arterial de manera correcta.",
                imageRes = R.drawable.ic_procedures6,
                onClick = {
                    dialogTitle = "Presión Arterial"
                    selectedSteps = stepsPresionArterial
                    showDialog = true
                }
            )

        }

        ActionButton(
            text = "Realizar Evaluación",
            onClick = { navController?.navigate(Routes.QUIZ_PROCEDIMIENTOS) },
            enabled = isButton1Enabled
        )

        // Botón 2: Evaluación para `subjectId = 5`
        ActionButton(
            text = "Realizar Evaluación 2",
            onClick = { navController?.navigate(Routes.QUIZ_PROC_TF) },
            enabled = isButton2Enabled
        )

        // Espacio adicional al final para desplazamiento cómodo
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

