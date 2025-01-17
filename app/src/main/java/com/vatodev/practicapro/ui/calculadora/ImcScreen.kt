package com.vatodev.practicapro.ui.calculadora

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vatodev.practicapro.components.ActionButton
import com.vatodev.practicapro.components.GenderToggleButton
import com.vatodev.practicapro.components.Table
import com.vatodev.practicapro.viewmodel.ImcViewModel

@Composable
fun ImcScreen(
    navController: NavController,
    viewModel: ImcViewModel = viewModel()
) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Hombre") }
    var showTable by remember { mutableStateOf(false) }

    val imcResult by viewModel.imcResult.collectAsState()
    val classification by viewModel.classification.collectAsState()

    // Contenedor de scroll vertical para toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Scroll vertical
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F7F7))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botones de selección de género
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GenderToggleButton(
                        label = "Hombre",
                        selected = selectedGender == "Hombre",
                        onClick = { selectedGender = "Hombre" }
                    )
                    GenderToggleButton(
                        label = "Mujer",
                        selected = selectedGender == "Mujer",
                        onClick = { selectedGender = "Mujer" }
                    )
                }

                // Campo de entrada para la edad
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter { char -> char.isDigit() } },
                    label = { Text("Edad (años)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo de entrada para el peso
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo de entrada para la altura
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Altura (m)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para calcular el IMC
        ActionButton(
            onClick = {
                val weightValue = weight.toDoubleOrNull()
                val heightValue = height.toDoubleOrNull()
                val ageValue = age.toIntOrNull()

                if (weightValue != null && heightValue != null && ageValue != null) {
                    viewModel.calculateImc(weightValue, heightValue, selectedGender, ageValue)
                }
            },
            text = "Calcular IMC"
        )

        // Botón para salir
        ActionButton(
            onClick = { navController.popBackStack() },
            text = "Regresar"
        )

        // Mostrar el resultado del IMC
        imcResult?.let { imc ->
            Text(
                text = "IMC: ${String.format("%.2f", imc)}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Mostrar la clasificación del IMC
        classification?.let {
            Text(
                text = "Clasificación: $it",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Botón para mostrar/ocultar la tabla
        ActionButton(
            onClick = { showTable = !showTable },
            text = if (showTable) "Ocultar Tabla de Clasificación" else "Mostrar Tabla de Clasificación"
        )

        if (showTable) {
            Table(
                headers = listOf("Clasificación", "IMC"),
                rows = listOf(
                    listOf("Bajo peso", "< 18.5"),
                    listOf("Peso normal", "18.5 - 24.9"),
                    listOf("Sobrepeso", "25.0 - 29.9"),
                    listOf("Obesidad grado 1", "30.0 - 34.9"),
                    listOf("Obesidad grado 2", "35.0 - 39.9"),
                    listOf("Obesidad grado 3", "≥ 40.0")
                )
            )
        }
    }
}
