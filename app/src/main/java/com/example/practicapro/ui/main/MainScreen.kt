package com.example.practicapro.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practicapro.R
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer
import com.example.practicapro.module.Module
import com.example.practicapro.module.ModuleCard
import kotlinx.coroutines.delay

// Función principal que incluye la animación
@Composable
fun AnimatedModuleCard(
    module: Module,
    modifier: Modifier = Modifier,
    delayMillis: Int,
    onClick: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(50f) }

    // Lanzamos las animaciones en paralelo
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 250)
        )
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 250)
        )
    }

    // Aplicar las animaciones al Composable de la tarjeta
    ModuleCard(
        module = module,
        modifier = modifier
            .graphicsLayer(
                translationY = offsetY.value, // Aplicar desplazamiento vertical animado
                alpha = alpha.value // Aplicar opacidad animada
            ),
        onClick = onClick
    )
}

// Reemplazar las tarjetas por la versión animada en MainScreen
@Composable
fun MainScreen(navController: NavController) {
    val scrollState = rememberScrollState() // Estado de desplazamiento

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_fm),
                contentDescription = "Logo UAM Facultad Medicina",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Contenido desplazable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp) // Asegura que el contenido no se superponga con el logo
                .padding(horizontal = 8.dp) // Padding lateral pequeño
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Relleno flexible superior
            Spacer(modifier = Modifier.weight(2f))

            // Módulo de calculadora ocupa ambas columnas
            AnimatedModuleCard(
                module = Module(
                    name = "Calculadora",
                    description = "Herramienta para realizar cálculos médicos.",
                    imageRes = R.drawable.ic_calculator
                ),
                modifier = Modifier.fillMaxWidth(),
                delayMillis = 100, // Retardo inicial para la animación
                onClick = { navController.navigate("calculadora") } // Redirecciona a Calculadora
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de módulos
            val modules = listOf(
                Module(
                    name = "Técnicas de Asepsia y Antisepsia",
                    description = "Aprende las técnicas básicas de asepsia.",
                    imageRes = R.drawable.ic_asepsia
                ),
                Module(
                    name = "Procedimientos Básicos",
                    description = "Guía para atención al paciente.",
                    imageRes = R.drawable.ic_procedures
                ),
                Module(
                    name = "Administración de Medicamentos",
                    description = "Conoce los aspectos básicos.",
                    imageRes = R.drawable.ic_medicines
                ),
                Module(
                    name = "Urgencias Médicas",
                    description = "Manejo inicial de urgencias médicas.",
                    imageRes = R.drawable.ic_emergency
                )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                modules.chunked(2).forEachIndexed { rowIndex, rowModules ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowModules.forEachIndexed { moduleIndex, module ->
                            AnimatedModuleCard(
                                module = module,
                                modifier = Modifier.weight(1f), // Distribución uniforme
                                delayMillis = 200 * (rowIndex * 2 + moduleIndex), // Incrementa el retardo para cada módulo
                                onClick = { // Redirección a las rutas correspondientes
                                    when (module.name) {
                                        "Técnicas de Asepsia y Antisepsia" -> navController.navigate("tecnicas")
                                        "Procedimientos Básicos" -> navController.navigate("procedimientos")
                                        "Administración de Medicamentos" -> navController.navigate("administracion")
                                        "Urgencias Médicas" -> navController.navigate("urgencias")
                                    }
                                }
                            )
                        }
                        if (rowModules.size < 2) {
                            Spacer(modifier = Modifier.weight(1f)) // Relleno para filas incompletas
                        }
                    }
                }
            }
            // Relleno flexible inferior
            Spacer(modifier = Modifier.weight(2f))
        }
    }
}


