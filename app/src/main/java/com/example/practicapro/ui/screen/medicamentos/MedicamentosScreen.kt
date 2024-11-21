package com.example.practicapro.ui.screen.medicamentos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.practicapro.R

@Composable
fun MedicamentosScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "Administración de Medicamentos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        // Subtítulo
        Text(
            text = "Aprende los principios básicos de la administración segura y efectiva de medicamentos.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        // Imagen representativa
        Image(
            painter = painterResource(id = R.drawable.ic_medicines), // Cambiar por una imagen adecuada
            contentDescription = "Administración de Medicamentos",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        // Sección de conceptos clave
        SectionTitle("Conceptos Clave")
        SectionContent(
            "La administración de medicamentos incluye los procedimientos para garantizar la entrega segura, eficaz y adecuada de medicamentos a los pacientes."
        )
        SectionContent(
            "Los principios básicos incluyen:\n" +
                    "- Paciente correcto\n" +
                    "- Medicamento correcto\n" +
                    "- Dosis correcta\n" +
                    "- Vía correcta\n" +
                    "- Hora correcta"
        )

        // Tipos de medicamentos
        SectionTitle("Tipos de Medicamentos")
        SectionContent(
            "- Analgésicos: Alivian el dolor.\n" +
                    "- Antibióticos: Combaten infecciones bacterianas.\n" +
                    "- Antiinflamatorios: Reducen inflamación y dolor.\n" +
                    "- Antisépticos: Previenen infecciones al desinfectar tejidos."
        )

        // Procedimientos básicos
        SectionTitle("Procedimientos Básicos")
        SectionContent(
            "Sigue estos pasos para una administración segura de medicamentos:\n" +
                    "1. Verificar la prescripción médica.\n" +
                    "2. Identificar correctamente al paciente.\n" +
                    "3. Preparar la dosis adecuada.\n" +
                    "4. Administrar utilizando la vía indicada.\n" +
                    "5. Monitorear al paciente después de la administración."
        )

        // Actividades dinámicas (introducción a los minijuegos)
        SectionTitle("Actividades Prácticas")
        SectionContent(
            "Pon a prueba tus conocimientos con actividades interactivas:\n" +
                    "- Emparejar conceptos: Relaciona medicamentos con sus usos.\n" +
                    "- Orden correcto: Organiza los pasos para administrar un medicamento de forma segura."
        )

        // Espacio adicional al final para mejor desplazamiento
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Componentes reutilizables
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
