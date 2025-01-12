package com.example.practicapro.ui.calculadora

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicapro.repository.UserRepository
import com.example.practicapro.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun CalculadoraScreen(userViewModel: UserViewModel = viewModel()) {
    val scope = rememberCoroutineScope()

    // Estado para mostrar el resultado
    var result by remember { mutableStateOf("Calculadora") }

    // Verificar si hay un token presente
    val token = userViewModel.token.value
    val tokenStatus = if (token.isNullOrEmpty()) "Token no encontrado" else "Token presente"

    // Log del token obtenido
    Log.d("CalculadoraScreen", "Token obtenido desde el ViewModel: $token")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = tokenStatus)

            Button(
                onClick = {
                    scope.launch {
                        // Realizar la llamada a la API
                        val response = UserRepository.getUserProfile()
                        response.fold(
                            onSuccess = { profile ->
                                result = "Perfil: ${profile.nombre}"
                            },
                            onFailure = { throwable ->
                                result = "Error: ${throwable.message}"
                            }
                        )
                    }
                }
            ) {
                Text(text = "Probar API")
            }

            Text(text = result)
        }
    }
}
