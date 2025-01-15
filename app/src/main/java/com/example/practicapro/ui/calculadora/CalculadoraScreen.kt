package com.example.practicapro.ui.calculadora

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practicapro.R
import com.example.practicapro.components.module.Module
import com.example.practicapro.components.module.ModuleCard

@Composable
fun CalculadoraScreen(navController: NavController) {
    // Contenedor principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta para IMC
        ModuleCard(
            module = Module(
                name = "Índice de Masa Corporal (IMC)",
                description = "Calcula el índice de masa corporal usando la fórmula: IMC = Peso (kg) / Altura^2 (m).",
                imageRes = R.drawable.ic_asepsia
            ),
            onClick = {
                navController.navigate("imc_screen") // Navegar a la pantalla de cálculo del IMC
            }
        )

        // Tarjeta para PAM
        ModuleCard(
            module = Module(
                name = "Presión Arterial Media (PAM)",
                description = "Calcula la presión arterial media usando la fórmula: PAM = (2 * PD + PS) / 3.",
                imageRes = R.drawable.ic_asepsia
            ),
            onClick = {
                navController.navigate("pam_screen") // Navegar a la pantalla de cálculo de la PAM
            }
        )
    }
}
