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

    val stepsTalla = listOf(
        "1. Informe al paciente las actividades que se van a realizar para que esté enterado y sea más fácil medirlo.",
        "2. Indique al sujeto que se quite el calzado, gorras, adornos y se suelte el cabello. Si es necesario ayúdele.",
        "3. Coloque a la persona debajo del estadiómetro de espalda a él con la mirada al frente, sobre una línea imaginaria vertical que divida su cuerpo en dos hemisferios.",
        "4. Verifique que los pies estén en posición correcta.",
        "5. Asegúrese que la cabeza, espalda, pantorrillas, talones y glúteos estén en contacto con la pared y sus brazos caigan naturalmente a lo largo del cuerpo.",
        "6. Acomode la cabeza en posición recta coloque la palma de la mano izquierda abierta sobre el mentón del sujeto, y suavemente cierre sus dedos.",
        "7. Trace una línea imaginaria (Plano de Frankfort) que va del orificio del oído a la base de la órbita del ojo. Esta línea debe ser paralela a la base del estadiómetro y formar un ángulo recto con respecto la pared.",
        "8. Si la marca del estadiómetro se encuentra entre un centímetro y otro, anote el valor que esté más próximo; si está a la mitad, se tomará el del centímetro anterior.",
        "9. Baje el estadiómetro y tome cuidadosamente la lectura en centímetros. Regístrelo."
    )

    val stepsPeso = listOf(
        "1. La medición se realizará con la menor ropa posible y sin zapatos.",
        "2. Se pide al sujeto que suba a la báscula colocando los pies paralelos en el centro, de frente al examinador.",
        "3. Debe estar erguido, con la vista hacia el frente, sin moverse y con los brazos que caigan naturalmente a los lados.",
        "4. Lectura:\n" +
                "   A. Si se emplea báscula de piso, se toma la lectura cuando el indicador de la báscula se encuentra completamente fijo.\n" +
                "   B. Si se usa báscula de plataforma, cuando la aguja central se encuentre en medio de los 2 márgenes y sin moverse, proceda a tomar la lectura.\n" +
                "   C. En caso de emplear báscula electrónica, se tomará la lectura del número que se encuentre parpadeando."
    )

    val stepsFrecuenciaCardiaca = listOf(
        "1. Informar al paciente y/o al cuidador principal del procedimiento a realizar y solicitarle su colaboración, " +
                "a ser posible, recalcando su utilidad, usando un lenguaje comprensible y resolviendo sus dudas y temores. " +
                "En el caso de pacientes pediátricos explicarles el procedimiento a los padres.",
        "2. Realizar higiene de manos.",
        "3. Ayudar al enfermo a adoptar una posición cómoda que permita el acceso a la zona elegida.",
        "4. Elegir el lugar de la medición: radial, braquial, carótida, temporal, femoral, tibial posterior, poplítea o pedia.",
        "5. Si se va a medir en la arteria radial, colocar la extremidad semiflexionada con la palma de la mano hacia arriba.",
        "6. Apoyar la yema de los dedos índice, corazón y anular de la mano dominante en el punto seleccionado y localizar el latido arterial. " +
                "El uso del pulgar está contraindicado porque tiene pulso propio que puede confundirse con el del paciente.",
        "7. Contar durante 15 segundos, multiplicando el resultado por cuatro. Si el pulso es irregular se cuenta durante un minuto completo y/o tomar pulso apical.",
        "8. Valorar el ritmo (regular o irregular), amplitud (fuerte o débil) y tensión (blando o duro).",
        "9. Registrar el valor."
    )


    val stepsFrecuenciaRespiratoria = listOf(
        "1. Comprobar la identidad del paciente. Respetar la intimidad del enfermo y guardar confidencialidad de sus datos.",
        "2. No es conveniente informarlo de que se le va a medir la frecuencia respiratoria, ya que podría cambiar involuntariamente el ritmo; " +
                "es conveniente medirla al mismo tiempo que otros signos vitales (pulso).",
        "3. Realizar higiene de manos.",
        "4. Colocar al paciente en una posición adecuada, sentado o acostado en posición de semi-fowler, que permita la contabilización de la frecuencia respiratoria. " +
                "El mal alineamiento corporal impide la correcta expansión torácica y disminuye la ventilación, e influye, por tanto, en la frecuencia y en el volumen respiratorio.",
        "5. Medir la frecuencia respiratoria:\n" +
                "   A. Observar los movimientos respiratorios, contar las elevaciones (inspiraciones) del tórax y/o abdomen durante 1 minuto.\n" +
                "   B. Colocar una mano en el tórax del paciente de manera que se puedan percibir los movimientos respiratorios. Contar las inspiraciones durante 1 minuto.\n" +
                "   C. Auscultación: colocar el estetoscopio en el tórax y contar el número de inspiraciones durante 1 minuto.\n" +
                "   D. Niños pequeños: cuantificar los movimientos de ascenso y descenso del abdomen (respiración diafragmática).",
        "6. Observar el ritmo (regular o irregular), profundidad y volumen (superficial o profundo) y características de la respiración, " +
                "también la coloración de piel y uñas y uso de músculos accesorios.",
        "7. Realizar higiene de manos, según procedimiento.",
        "8. Registre el valor."
    )

    val stepsPresionArterial = listOf(
        "1. Compruebe que el nivel de mercurio del esfigmomanómetro es 0 o, en caso de un dispositivo aneroide o híbrido, que la aguja se encuentra dentro de la ventana de calibración.",
        "2. Palpe la arteria humeral y coloque el manguito de forma que la línea media de la bomba se encuentre encima de la pulsación arterial. El manguito debe estar aproximadamente a nivel horizontal al corazón.",
        "3. Coloque el manguito alrededor del brazo desnudo del paciente y asegúrelo firmemente. El margen inferior del manguito debería estar a unos 2 cm por encima del pliegue antecubital, lugar donde se coloca la campana del estetoscopio.",
        "4. Coloque el manómetro a nivel de los ojos para asegurar una lectura precisa. Asegúrese de que los tubos conectados al manguito no estén obstruidos.",
        "5. Infle el manguito rápidamente hasta 70 mmHg, luego aumente en incrementos de 10 mmHg mientras palpa el pulso radial. Observe el nivel de presión donde desaparece el pulso.",
        "6. Coloque los auriculares del estetoscopio en los oídos con una ligera inclinación hacia adelante para un ajuste correcto.",
        "7. Posicione la campana del estetoscopio ligeramente por encima de la arteria humeral, justo medial al pliegue antecubital.",
        "8. Infle el manguito con rapidez y constancia hasta 20-30 mmHg por encima del nivel donde el pulso desapareció. Luego desinfle lentamente (a 2 mmHg por segundo) mientras escucha los sonidos de Korotkov.",
        "9. Información para retroalimentación:\n" +
                "   - Fase I: La aparición de sonidos claros repetitivos, como de golpeteo, que corresponde con la aparición del pulso palpable.\n" +
                "   - Fase II: Sonidos más suaves y prolongados.\n" +
                "   - Fase III: El sonido se hace más intenso y abrupto.\n" +
                "   - Fase IV: Sonidos amortiguados y suaves.\n" +
                "   - Fase V: Los sonidos desaparecen por completo y se registra como el último sonido audible.",
        "10. Registre la presión sistólica y diastólica inmediatamente, redondeando hacia arriba al valor más cercano a 2 mmHg. También registre el brazo medido, la posición del paciente, y el tamaño del manguito utilizado."
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

