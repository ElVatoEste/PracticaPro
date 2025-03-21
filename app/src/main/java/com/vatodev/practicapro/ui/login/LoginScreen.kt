package com.vatodev.practicapro.ui.login

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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.NormalTextField
import com.vatodev.practicapro.components.PasswordTextField
import com.vatodev.practicapro.components.VerificationCodeModal
import com.vatodev.practicapro.components.ResetPasswordModal
import com.vatodev.practicapro.viewmodel.ResetPasswordViewModel
import com.vatodev.practicapro.viewmodel.VerificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    context: android.content.Context,
    viewModel: LoginViewModel = viewModel(),
    verificationViewModel: VerificationViewModel = viewModel(),
    resetViewModel: ResetPasswordViewModel = viewModel()
) {
    val email by viewModel.email
    val password by viewModel.password
    val error by viewModel.error
    val isLoading by viewModel.isLoading
    val showEmailNotConfirmedDialog by viewModel.showEmailNotConfirmedDialog
    val focusManager = LocalFocusManager.current

    // Estado para el modal de restablecer contraseña
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    // Animaciones
    val logoOffsetY = remember { Animatable(0f) }
    val logoScale = remember { Animatable(1f) }
    val inputsAlpha = remember { Animatable(0f) }

    // Inicialización de animaciones
    LaunchedEffect(Unit) {
        delay(200)
        logoOffsetY.animateTo(
            targetValue = -140f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        inputsAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    // Helpers
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
                    .padding(top = 150.dp)
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Iniciar Sesión", style = MaterialTheme.typography.titleLarge)

                // Campo de correo
                NormalTextField(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de contraseña
                PasswordTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de inicio de sesión
                Button(
                    onClick = {
                        if (!isLoading) {
                            viewModel.doLogin(
                                context = context,
                                onLoginSuccess = onLoginSuccess,
                                onErrorSnackBar = { message ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
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
                        Text("Iniciar Sesión")
                    }
                }

                // Texto para restablecer contraseña
                TextButton(onClick = { showResetPasswordDialog = true }) {
                    Text(
                        text = "Olvidé mi contraseña",
                        color = Color(0xFF7DBB00),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Texto para crear una cuenta
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "¿No tienes cuenta? Crea una aquí",
                        color = Color(0xFF7DBB00),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Mostrar errores si existen
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    // Modal de verificación de correo
    if (showEmailNotConfirmedDialog) {
        VerificationCodeModal(
            isVisible = showEmailNotConfirmedDialog,
            onDismiss = { viewModel.dismissEmailNotConfirmedDialog() },
            viewModel = verificationViewModel,
            email = email,
            context = context,
            onConfirmSuccess = {
                scope.launch {
                    viewModel.dismissEmailNotConfirmedDialog()
                    snackbarHostState.showSnackbar(
                        "Correo confirmado exitosamente",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }

    // Modal de restablecimiento de contraseña
    if (showResetPasswordDialog) {
        ResetPasswordModal(
            isVisible = showResetPasswordDialog,
            onDismiss = { showResetPasswordDialog = false },
            context = context,
            viewModel = resetViewModel,
            onResetSuccess = { resetEmail ->
                scope.launch {
                    showResetPasswordDialog = false
                    snackbarHostState.showSnackbar(
                        "Se ha enviado el correo de restablecimiento a: $resetEmail",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }
}
