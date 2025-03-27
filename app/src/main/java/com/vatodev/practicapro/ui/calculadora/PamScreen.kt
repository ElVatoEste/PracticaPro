package com.vatodev.practicapro.ui.calculadora

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vatodev.practicapro.components.general.ActionButton
import com.vatodev.practicapro.viewmodel.PamViewModel

@Composable
fun PamScreen(navController: NavController, viewModel: PamViewModel = viewModel()) {
    var systolicPressure by remember { mutableStateOf("") }
    var diastolicPressure by remember { mutableStateOf("") }

    val pamResult by viewModel.pamResult.collectAsState()
    val classification by viewModel.classification.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Calculadora de Presión Arterial Media (PAM)",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de entrada para la Presión Sistólica
        OutlinedTextField(
            value = systolicPressure,
            onValueChange = { systolicPressure = it.filter { char -> char.isDigit() || char == '.' } },
            label = { Text("Presión Arterial Sistólica (mmHg)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de entrada para la Presión Diastólica
        OutlinedTextField(
            value = diastolicPressure,
            onValueChange = { diastolicPressure = it.filter { char -> char.isDigit() || char == '.' } },
            label = { Text("Presión Arterial Diastólica (mmHg)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para calcular la PAM
        ActionButton(
            onClick = {
                val psValue = systolicPressure.toDoubleOrNull()
                val pdValue = diastolicPressure.toDoubleOrNull()

                if (psValue != null && pdValue != null) {
                    viewModel.calculatePam(psValue, pdValue)
                }
            },
            text = "Calcular PAM"
        )

        // Botón para regresar
        ActionButton(
            onClick = { navController.popBackStack() },
            text = "Regresar"
        )

        // Mostrar el resultado
        pamResult?.let { pam ->
            Text(
                text = "PAM: ${String.format("%.2f", pam)} mmHg",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Mostrar la clasificación
        classification?.let { result ->
            Text(
                text = "Clasificación: $result",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
