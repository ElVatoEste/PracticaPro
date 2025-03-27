package com.vatodev.practicapro.ui.calculadora

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.module.Module
import com.vatodev.practicapro.components.module.ModuleCard
import com.vatodev.practicapro.navigation.Routes

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
                imageRes = R.drawable.ic_cal1
            ),
            onClick = {
                navController.navigate(Routes.IMC)
            }
        )

        // Tarjeta para PAM
        ModuleCard(
            module = Module(
                name = "Presión Arterial Media (PAM)",
                description = "Calcula la presión arterial media usando la fórmula: PAM = (2 * PD + PS) / 3.",
                imageRes = R.drawable.ic_cal2
            ),
            onClick = {
                navController.navigate(Routes.PAM)
            }
        )
    }
}
