package com.example.practicapro.ui.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.practicapro.R
import kotlinx.coroutines.delay

@Composable
fun MainScreen(navController: NavController) {
    val scrollState = rememberScrollState() // Estado de desplazamiento
    var isVisible by remember { mutableStateOf(false) } // Estado de visibilidad para la animación

    // Lanzamos una animación de entrada con retardo
    LaunchedEffect(Unit) {
        delay(300) // Retardo de 300ms antes de mostrar los elementos
        isVisible = true
    }

    Box {
        // Contenedor para el logo sticky
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(140.dp)) // Espacio reservado para el logo

            // Módulo de calculadora ocupa ambas columnas
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(), // Efecto de entrada
                exit = fadeOut()  // Efecto de salida
            ) {
                ModuleCard(
                    module = Module(
                        name = "Calculadora",
                        description = "Herramienta para realizar cálculos médicos.",
                        imageRes = R.drawable.ic_calculator
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    navController.navigate("calculadora") // Redirección al módulo Calculadora
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                modules.chunked(2).forEach { rowModules ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowModules.forEach { module ->
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                ModuleCard(
                                    module = module,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    navController.navigate(module.name.replace(" ", "_").lowercase())
                                }
                            }
                        }
                        if (rowModules.size < 2) {
                            Spacer(modifier = Modifier.weight(1f)) // Relleno para filas incompletas
                        }
                    }
                }
            }
        }

        // Logo sticky en la parte superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_fm),
                contentDescription = "Logo UAM Facultad Medicina",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
    }
}

// Modelo de datos para un módulo
data class Module(val name: String, val description: String, val imageRes: Int)

// Composable para las tarjetas de módulos
@Composable
fun ModuleCard(module: Module, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick, // Habilita la funcionalidad cicleable
        modifier = modifier
            .height(220.dp), // Altura ajustada
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5) // Fondo claro
        ),
        elevation = CardDefaults.cardElevation(8.dp), // Efecto de elevación
        shape = RoundedCornerShape(12.dp) // Esquinas redondeadas
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen que ocupa la parte superior y es cuadrada
            Image(
                painter = painterResource(id = module.imageRes),
                contentDescription = module.name,
                contentScale = ContentScale.Crop, // La imagen llena el espacio
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Título
            Text(
                text = module.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            // Descripción
            Text(
                text = module.description,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}
