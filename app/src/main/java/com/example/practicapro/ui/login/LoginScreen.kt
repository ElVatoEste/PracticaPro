package com.example.practicapro.ui.login

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.practicapro.R
import com.example.practicapro.network.NetworkObserver
import com.example.practicapro.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, context: android.content.Context) {
    // Estados del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Animaciones
    val logoOffsetY = remember { Animatable(0f) }
    val logoScale = remember { Animatable(1f) }
    val inputsAlpha = remember { Animatable(0f) }

    // Inicialización de animaciones
    LaunchedEffect(Unit) {
        delay(500)
        // Primero, movemos el logo hacia arriba
        logoOffsetY.animateTo(
            targetValue = -100f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        // Escalamos el logo a 1f (si deseas otro efecto, puedes cambiarlo)
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        // Ahora mostramos los inputs
        inputsAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    // Observadores y helpers
    val isNetworkAvailable by NetworkObserver.isNetworkAvailable.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Logo animado en el centro
            Image(
                painter = painterResource(id = R.drawable.logo_fm),
                contentDescription = "Logo UAM Facultad Medicina",
                modifier = Modifier
                    .offset(y = logoOffsetY.value.dp)
                    .scale(logoScale.value)
                    .fillMaxWidth(0.8f)
                    .aspectRatio(2f, matchHeightConstraintsFirst = false)
            )

            // Inputs debajo del logo, apareciendo después
            Column(
                modifier = Modifier
                    .alpha(inputsAlpha.value)
                    // Agregamos un padding top para que aparezcan debajo del logo
                    .padding(top = 150.dp)
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Iniciar Sesión", style = MaterialTheme.typography.titleLarge)

                // Campo de correo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de inicio de sesión
                Button(
                    onClick = {
                        if (!isLoading) { // Validación de múltiples clics
                            scope.launch {
                                if (!isNetworkAvailable) {
                                    snackbarHostState.showSnackbar(
                                        "Sin conexión a Internet. Verifica tu conexión.",
                                        duration = SnackbarDuration.Short
                                    )
                                } else {
                                    isLoading = true
                                    val result = AuthRepository.login(context, email, password)
                                    isLoading = false
                                    result.fold(
                                        onSuccess = {
                                            onLoginSuccess() // Login exitoso
                                        },
                                        onFailure = {
                                            error = "Error: ${it.localizedMessage}"
                                            snackbarHostState.showSnackbar(
                                                error ?: "Ocurrió un error",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7DBB00),    // Color del fondo del botón
                        contentColor = Color.White             // Color del texto/icono dentro del botón
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF7DBB00)
                        )
                    } else {
                        Text("Iniciar Sesión")
                    }
                }

                // Mostrar errores si existen
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
