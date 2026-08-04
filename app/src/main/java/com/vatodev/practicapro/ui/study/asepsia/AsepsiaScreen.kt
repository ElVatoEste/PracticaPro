package com.vatodev.practicapro.ui.study.asepsia

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.vatodev.practicapro.components.general.ActionButton
import com.vatodev.practicapro.components.general.VideoPlayerScreen
import com.vatodev.practicapro.components.module.SectionContent
import com.vatodev.practicapro.components.module.SectionTitle
import com.vatodev.practicapro.components.general.MultiStepDialog
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.viewmodel.helper.DialogState

@Composable
fun AsepsiaScreen(navController: NavController) {

    val context = LocalContext.current
    var isButtonEnabled by remember { mutableStateOf(false) }

    // Estado del diálogo agrupado en un solo objeto
    var dialogState by remember {
        mutableStateOf(
            DialogState(
                showDialog = false,
                title = "",
                steps = emptyList()
            )
        )
    }

    LaunchedEffect(key1 = navController.currentBackStackEntry) {
        val database = DatabaseProvider.getDatabase(context)
        val noteDao = database.noteDao()
        isButtonEnabled = !noteDao.hasReachedMaxAttempts(1)
        Log.d("AsepsiaScreen", "Estado del botón: $isButtonEnabled")
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
            color = Color.Black
        )

        // Imagen representativa
        Image(
            painter = painterResource(id = R.drawable.ic_asepsia2),
            contentDescription = "Imagen de Asepsia",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        // Sección de conceptos clave
        SectionTitle("Conceptos Básicos")
        SectionContent(
            "La asepsia incluye prácticas para prevenir la introducción de microorganismos en áreas críticas. Esto es fundamental en el entorno médico para proteger a los pacientes y al personal de salud."
        )

        // Reproductor de video
        SectionTitle("Video Lavado de manos clínico")
        VideoPlayerScreen(
            videoAspectRatio = 1f,
            videoUri = "android.resource://${LocalContext.current.packageName}/${R.raw.videotutorial}"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Sección de técnicas
        SectionTitle("Procedimientos Antisepticos")
        TechniqueCardList { title, steps ->
            dialogState = dialogState.copy(
                showDialog = true,
                title = title,
                steps = steps
            )
        }

        ActionButton(
            text = "Realizar Evaluación",
            onClick = { navController.navigate("quiz_screen") },
            enabled = isButtonEnabled
        )

        Spacer(modifier = Modifier.weight(1f))
    }

    // Diálogo para mostrar pasos
    if (dialogState.showDialog) {
        MultiStepDialog(
            title = dialogState.title,
            steps = dialogState.steps,
            onDismiss = {
                dialogState = dialogState.copy(showDialog = false)
            }
        )
    }
}



