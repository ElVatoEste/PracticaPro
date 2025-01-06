package com.example.practicapro.ui.study.asepsia

import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavController
import com.example.practicapro.R
import com.example.practicapro.components.*

@Composable
fun AsepsiaScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var selectedSteps by remember { mutableStateOf(emptyList<String>()) }

    // Pasos para cada técnica
    val stepsLavadoClinico = listOf(
        "1. Retire las joyas y suba las mangas arriba del codo.",
        "2. Moje sus manos y antebrazos completamente.",
        "3. Enjabonar manos, muñecas y antebrazos.",
        "4. Frotar las manos, muñecas y antebrazos friccionando especialmente en los espacios interdigitales y las uñas.",
        "5. Limpie las uñas y frote las yemas de los dedos con la palma de la mano contraria.",
        "6. Frote vigorosamente durante 40 a 60 segundos.   ",
        "7. Enjuague con abundante agua.",
        "8. Seque las manos y antebrazo con toalla desechable.",
        "9. Cierre la llave utilizando la toalla de papel con la que se secó (en caso de no contar con la grifería recomendada)",
        "10. Deseche la toalla en el recipiente de desecho común."
    )

    val stepsLavadoQuirurgico = listOf(
        "1. Quítese las joyas de las manos y muñecas y moje completamente sus manos y\n" +
                "antebrazos. La duración total de este procedimiento es de 3 a 5 minutos.",
        "2. Utilice de tres a cinco mililitros de jabón antiséptico para cada mano o use dos\n" +
                "aplicaciones del dispensador de jabón y limpie la región debajo de las uñas para\n" +
                "eliminar las bacterias acumuladas, luego frótese a cada lado de cada dedo,\n" +
                "entre los dedos, el dorso y la palma de la mano. El jabón debe estar en contacto\n" +
                "con la piel durante tres a seis minutos",
        "3. Proceda con un movimiento circular a frotarse iniciando en la punta de los dedos\n" +
                "de una mano y lave haciendo espuma entre los dedos, continuando desde la\n" +
                "punta de los dedos hasta el codo, haga lo mismo con la otra mano y brazo y\n" +
                "continúe lavando por aproximadamente dos minutos más.",
        "4. Enjuague cada brazo separadamente empezando con la punta de los dedos,\n" +
                "cada lado del brazo hasta tres pulgadas por encima del codo, el tiempo que sea\n" +
                "necesario.",
        "5. Repita el proceso en la otra mano y el otro antebrazo, manteniendo la mano por\n" +
                "encima del codo todo el tiempo. Si por alguna razón la mano toca cualquier\n" +
                "elemento, el lavado de manos se prolongará un minuto más en el área\n" +
                "contaminada.",
        "6. Enjuague las manos y los brazos pasándolas por el agua en una sola dirección,\n" +
                "desde la punta de los dedos hasta los codos. No mueva los brazos hacia atrás, y\n" +
                "hacia delante mientras los enjuaga.",
        "7. Frotar con una solución alcohólica como mínimo durante 1,5 minutos.",
        "8. Diríjase a la sala de operaciones, sosteniendo las manos por encima de los\n" +
                "codos."
    )

    val stepsUsoGuantes = listOf(
        "1. Realizar higiene de manos.",
        "2. Abrir el envoltorio externo sobre un campo estéril, con técnica aséptica.",
        "3. Abrir envoltura interna, manipulando sólo los extremos.",
        "4. Observar que las palmas de las manos de los guantes miren hacia arriba con los\n" +
                "pulgares hacia fuera y los puños doblados.",
        "5. Enguantar mano derecha.",
        "6. Tomar la parte del puño doblado hacia fuera del guante derecho con los dedos\n" +
                "de la mano izquierda. No tocar el envoltorio ni la parte externa del guante.",
        "7. Levantar el guante de la mesa, introducir la mano derecha con la palma hacia\n" +
                "arriba orientada hacia la palma del guante.",
        "8. Dejar el puño del guante dado vuelta hasta colocar el otro guante.",
        "9. Enguantar la mano izquierda.",
        "10. Tomar el guante con la mano enguantada por debajo del puño doblado hacia\n" +
                "fuera.",
        "11. Mantener la palma hacia arriba, deslizar la mano izquierda dentro del guante.",
        "12. Dar vuelta el puño de ambos guantes sin tocar la piel."
    )

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
            color = Color.Gray
        )

        // Imagen representativa
        Image(
            painter = painterResource(id = R.drawable.ic_asepsia),
            contentDescription = "Imagen de Asepsia",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        // Reproductor de video
        SectionTitle("Video Introductorio")
        VideoPlayerScreen()

        // Sección de conceptos clave
        SectionTitle("Conceptos Básicos")
        SectionContent(
            "La asepsia incluye prácticas para prevenir la introducción de microorganismos en áreas críticas. Esto es fundamental en el entorno médico para proteger a los pacientes y al personal de salud."
        )

        // Sección de técnicas
        SectionTitle("Técnicas Básicas")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechniqueCard(
                title = "Lavado de Manos Clínico",
                description = "Pasos para un lavado de manos correcto.",
                imageRes = R.drawable.ic_asepsia,
                onClick = {
                    dialogTitle = "Lavado de Manos Clínico"
                    selectedSteps = stepsLavadoClinico
                    showDialog = true
                }
            )
            TechniqueCard(
                title = "Lavado de Manos Quirúrgico",
                description = "Elimina la flora transitoria y eliminar al máximo la flora residente de las manos, previo a\n" +
                        "la realización de un procedimiento invasivo que, por su especificidad o duración,\n" +
                        "requiera un alto grado de asepsia y un efecto residual.",
                imageRes = R.drawable.ic_asepsia,
                onClick = {
                    dialogTitle = "Lavado de Manos Quirúrgico"
                    selectedSteps = stepsLavadoQuirurgico
                    showDialog = true
                }
            )
            TechniqueCard(
                title = "Uso de Guantes",
                description = "Conoce el uso correcto del equipo de protección personal.",
                imageRes = R.drawable.ic_asepsia,
                onClick = {
                    dialogTitle = "Uso de Guantes"
                    selectedSteps = stepsUsoGuantes
                    showDialog = true
                }
            )
        }

        // Botón para evaluación
        ActionButton(
            text = "Realizar Evaluación",
            onClick = { navController.navigate("quiz_screen") } // Navega al quiz
        )

        // Espacio adicional al final para mejor desplazamiento
        Spacer(modifier = Modifier.height(32.dp))
    }

    // Diálogo para mostrar pasos
    if (showDialog) {
        MultiStepDialog(
            title = dialogTitle,
            steps = selectedSteps,
            onDismiss = { showDialog = false }
        )
    }
}
