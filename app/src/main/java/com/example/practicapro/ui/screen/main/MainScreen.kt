package com.example.practicapro.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun MainScreen(navController: NavController) {
    val scrollState = rememberScrollState() // Estado de desplazamiento

    Box(modifier = Modifier.fillMaxSize()) {
        // Banner sticky en la parte superior
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
            ModuleCard(
                module = Module(
                    name = "Calculadora",
                    description = "Herramienta para realizar cálculos médicos.",
                    imageRes = R.drawable.ic_calculator
                ),
                modifier = Modifier.fillMaxWidth(), // Ocupa el ancho completo
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
                modules.chunked(2).forEach { rowModules ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowModules.forEach { module ->
                            ModuleCard(
                                module = module,
                                modifier = Modifier.weight(1f), // Distribución uniforme
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

// Modelo de datos para un módulo
data class Module(val name: String, val description: String, val imageRes: Int)

// Composable para las tarjetas de módulos
@Composable
fun ModuleCard(module: Module, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick, // Habilita la funcionalidad clicable
        modifier = modifier
            .height(240.dp), // Altura ajustada
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
            // Imagen que ocupa la parte superior
            Image(
                painter = painterResource(id = module.imageRes),
                contentDescription = module.name,
                contentScale = ContentScale.Crop, // La imagen llena el espacio
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp) // Imagen cuadrada
            )

            // Contenedor para título y descripción
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // La sección inferior ocupa el espacio restante
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.SpaceEvenly // Espaciado uniforme entre título y descripción
            ) {
                // Título ocupa la mitad del espacio
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = module.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                // Descripción ocupa la otra mitad
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = module.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// Preview de la pantalla principal
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController() // Simulación de NavController
    MainScreen(navController = navController)
}
