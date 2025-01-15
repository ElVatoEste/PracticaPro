package com.example.practicapro.ui.study.medicamentos

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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.practicapro.R
import com.example.practicapro.components.*
import com.example.practicapro.components.navigation.Routes
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import com.example.practicapro.rooms.entitys.Note
import kotlinx.coroutines.launch

@Composable
fun MedicamentosScreen(navController: NavController) {


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

        // Reproductor de video
        SectionTitle("Video Educativo")
        VideoPlayerScreen()

        // Sección de conceptos clave
        SectionTitle("Conceptos Clave")
        SectionContent(
            "La administración de medicamentos implica garantizar que los pacientes reciban el tratamiento adecuado, en la dosis correcta y por la vía apropiada."
        )
        SectionContent(
            "Los principios básicos incluyen:\n" +
                    "- Paciente correcto\n" +
                    "- Medicamento correcto\n" +
                    "- Dosis correcta\n" +
                    "- Vía correcta\n" +
                    "- Hora correcta"
        )

        // Técnicas específicas
        SectionTitle("Técnicas Básicas")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechniqueCard(
                title = "Preparación de Medicamentos",
                description = "Cómo garantizar que la dosis y el tipo de medicamento sean los correctos.",
                imageRes = R.drawable.ic_medicines
            )
            TechniqueCard(
                title = "Administración Intramuscular",
                description = "Pasos para inyecciones seguras y efectivas.",
                imageRes = R.drawable.ic_medicines
            )
            TechniqueCard(
                title = "Vías Intravenosas",
                description = "Procedimientos para colocar una vía intravenosa correctamente.",
                imageRes = R.drawable.ic_medicines
            )
        }

        // Botón para minijuegos
        ActionButton(
            text = "Iniciar Actividad Interactiva",
            onClick = { navController.navigate(Routes.MINIJUEGO_MEDICAMENTOS) } // Navega al minijuego usando la constante
        )

        // Espacio adicional al final para mejor desplazamiento
        Spacer(modifier = Modifier.height(32.dp))
    }
}