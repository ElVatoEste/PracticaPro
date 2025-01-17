package com.vatodev.practicapro.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.AnimatedModuleCard
import com.vatodev.practicapro.components.module.Module
import kotlinx.coroutines.delay

@Composable
fun MainScreen(navController: NavController) {
    var isLoaded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        delay(650)
        isLoaded = true
    }

    if (!isLoaded) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {}
    } else {
        // Contenido principal
        Box(modifier = Modifier.fillMaxSize()) {

            // Logo en la parte superior
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

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp)
                    .padding(horizontal = 8.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(2f))

                // Tarjeta animada para la Calculadora
                AnimatedModuleCard(
                    module = Module(
                        name = "Calculadora",
                        description = "Herramienta para realizar cálculos médicos.",
                        imageRes = R.drawable.ic_calculator
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    delayMillis = 100,
                    onClick = { navController.navigate("calculadora") }
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
                                    modifier = Modifier.weight(1f),
                                    delayMillis = 200 * (rowIndex * 2 + moduleIndex),
                                    onClick = {
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
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
