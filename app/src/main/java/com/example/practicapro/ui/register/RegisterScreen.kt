package com.example.practicapro.ui.register

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
fun RegisterScreen(onRegisterSuccess: () -> Unit, context: android.content.Context) {
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Animaciones
    val logoOffsetY = remember { Animatable(0f) }
    val logoScale = remember { Animatable(1f) }
    val inputsAlpha = remember { Animatable(0f) }

    // Inicialización de animaciones
    LaunchedEffect(Unit) {
        delay(500)
        logoOffsetY.animateTo(
            targetValue = -100f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
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
            // Logo animado
            Image(
                painter = painterResource(id = R.drawable.logo_fm),
                contentDescription = "Logo Registro",
                modifier = Modifier
                    .offset(y = logoOffsetY.value.dp)
                    .scale(logoScale.value)
                    .fillMaxWidth(0.8f)
                    .aspectRatio(2f, matchHeightConstraintsFirst = false)
            )

            // Inputs debajo del logo
            Column(
                modifier = Modifier
                    .alpha(inputsAlpha.value)
                    .padding(top = 150.dp)
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Registro", style = MaterialTheme.typography.titleLarge)

                // Campo de nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

                // Confirmar contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de registro
                Button(
                    onClick = {
                        if (!isLoading) {
                            scope.launch {
                                if (password != confirmPassword) {
                                    snackbarHostState.showSnackbar(
                                        "Las contraseñas no coinciden",
                                        duration = SnackbarDuration.Short
                                    )
                                    return@launch
                                }

                                if (!isNetworkAvailable) {
                                    snackbarHostState.showSnackbar(
                                        "Sin conexión a Internet. Verifica tu conexión.",
                                        duration = SnackbarDuration.Short
                                    )
                                } else {
                                    isLoading = true
                                    val result = AuthRepository.register(context, nombre, email, password)
                                    isLoading = false
                                    result.fold(
                                        onSuccess = {
                                            snackbarHostState.showSnackbar(
                                                "Registro exitoso",
                                                duration = SnackbarDuration.Short
                                            )
                                            onRegisterSuccess() // Registro exitoso
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
                        containerColor = Color(0xFF7DBB00),
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF7DBB00)
                        )
                    } else {
                        Text("Registrarse")
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
