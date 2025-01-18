package com.vatodev.practicapro.ui.register

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.NormalTextField
import com.vatodev.practicapro.components.PasswordTextField
import com.vatodev.practicapro.components.VerificationCodeModal
import com.vatodev.practicapro.network.NetworkObserver
import com.vatodev.practicapro.viewmodel.RegisterViewModel
import com.vatodev.practicapro.viewmodel.VerificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen(
        onRegisterSuccess = {},
        onNavigateToLogin = {},
        context = LocalContext.current
    )
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    context: Context,
    registerViewModel: RegisterViewModel = viewModel(),
    verificationViewModel: VerificationViewModel = viewModel()
) {
    val nombre by registerViewModel.nombre
    val email by registerViewModel.email
    val password by registerViewModel.password
    val confirmPassword by registerViewModel.confirmPassword
    val error by registerViewModel.error
    val isLoading by registerViewModel.isLoading

    val focusManager = LocalFocusManager.current

    // Animaciones
    val logoOffsetY = remember { Animatable(0f) }
    val logoScale = remember { Animatable(1f) }
    val inputsAlpha = remember { Animatable(0f) }

    // Mostrar el modal de verificación de código
    val showVerificationModal = remember { mutableStateOf(false) }

    // Variable que controla si el usuario acepta la política de privacidad
    var isPrivacyPolicyAccepted by remember { mutableStateOf(false) }

    // Lanzamos animaciones
    LaunchedEffect(Unit) {
        delay(200)
        logoOffsetY.animateTo(
            targetValue = -180f,
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

    // Observadores y helpers
    val isNetworkAvailable by NetworkObserver.isNetworkAvailable.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val localContext = LocalContext.current

    // Función para abrir el enlace en el navegador
    fun openPrivacyPolicy(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vatodev.xyz/privacy"))
        context.startActivity(intent)
    }

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

            // Inputs
            Column(
                modifier = Modifier
                    .alpha(inputsAlpha.value)
                    .padding(top = 150.dp)
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Registro", style = MaterialTheme.typography.titleLarge)

                NormalTextField(
                    value = nombre,
                    onValueChange = { registerViewModel.onNombreChange(it) },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                NormalTextField(
                    value = email,
                    onValueChange = { registerViewModel.onEmailChange(it) },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PasswordTextField(
                    value = password,
                    onValueChange = { registerViewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = { registerViewModel.onConfirmPasswordChange(it) },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sección con Checkbox y enlace a la Política de Privacidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPrivacyPolicyAccepted,
                        onCheckedChange = { isPrivacyPolicyAccepted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF7DBB00),
                            uncheckedColor = Color.Gray
                        )
                    )

                    // Texto que incluye el link "Política de Privacidad"
                    val annotatedLinkString = buildAnnotatedString {
                        append("Al crear una cuenta, aceptas la ")
                        withStyle(style = SpanStyle(color = Color(0xFF7DBB00))) {
                            append("Política de Privacidad")
                        }
                    }

                    // ClickableText para abrir el enlace
                    ClickableText(
                        text = annotatedLinkString,
                        onClick = {
                            // Cuando el usuario hace click en "Política de Privacidad"
                            openPrivacyPolicy(localContext)
                        },
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!isLoading) {
                            scope.launch {
                                if (!isNetworkAvailable) {
                                    snackbarHostState.showSnackbar(
                                        "Sin conexión a Internet. Verifica tu conexión.",
                                        duration = SnackbarDuration.Short
                                    )
                                } else {
                                    registerViewModel.doRegister(
                                        onRegisterSuccess = { message ->
                                            scope.launch {
                                                val snackbarResult = snackbarHostState.showSnackbar(
                                                    message = message,
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (snackbarResult == SnackbarResult.Dismissed ||
                                                    snackbarResult == SnackbarResult.ActionPerformed) {
                                                    showVerificationModal.value = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    enabled = !isLoading && isPrivacyPolicyAccepted,
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
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "¿Ya tienes cuenta? Logeate aquí",
                        color = Color(0xFF7DBB00),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showVerificationModal.value) {
        VerificationCodeModal(
            isVisible = showVerificationModal.value,
            onDismiss = { showVerificationModal.value = false },
            viewModel = verificationViewModel,
            email = email,
            context = context,
            onConfirmSuccess = {
                showVerificationModal.value = false
                onRegisterSuccess()
            }
        )
    }
}
