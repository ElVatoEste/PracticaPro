package com.vatodev.practicapro.ui.register

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.general.BotonPrimario
import com.vatodev.practicapro.components.general.BotonSecundario
import com.vatodev.practicapro.components.general.Etiqueta
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.NormalTextField
import com.vatodev.practicapro.components.general.PasswordTextField
import com.vatodev.practicapro.ui.theme.LocalEstado
import com.vatodev.practicapro.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

private const val URL_PRIVACIDAD = "https://vatodev.xyz/privacy"

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    registerViewModel: RegisterViewModel = viewModel()
) {
    val nombre by registerViewModel.nombre
    val email by registerViewModel.email
    val password by registerViewModel.password
    val confirmPassword by registerViewModel.confirmPassword
    val error by registerViewModel.error
    val isLoading by registerViewModel.isLoading

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val localContext = LocalContext.current
    val estado = LocalEstado.current

    var aceptaPrivacidad by remember { mutableStateOf(false) }

    val puedeRegistrar = !isLoading &&
        aceptaPrivacidad &&
        nombre.isNotBlank() &&
        email.isNotBlank() &&
        password.isNotBlank() &&
        password == confirmPassword

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // El logo institucional es a color y no admite el tinte de marca.
            Image(
                painter = painterResource(R.drawable.logo_fm),
                contentDescription = "Universidad Americana, Facultad de Medicina",
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .widthIn(max = 260.dp)
            )

            Spacer(Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Etiqueta("Crear cuenta")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "El registro es local. Tus datos no salen del dispositivo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = estado.textoSuave
                )

                Spacer(Modifier.height(24.dp))

                NormalTextField(
                    value = nombre,
                    onValueChange = registerViewModel::onNombreChange,
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
                Spacer(Modifier.height(12.dp))
                NormalTextField(
                    value = email,
                    onValueChange = registerViewModel::onEmailChange,
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
                Spacer(Modifier.height(12.dp))
                PasswordTextField(
                    value = password,
                    onValueChange = registerViewModel::onPasswordChange,
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
                Spacer(Modifier.height(12.dp))
                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = registerViewModel::onConfirmPasswordChange,
                    label = { Text("Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Las contraseñas no coinciden.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = estado.error
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = aceptaPrivacidad,
                        onCheckedChange = { aceptaPrivacidad = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = estado.progreso,
                            checkmarkColor = MaterialTheme.colorScheme.background,
                            uncheckedColor = estado.textoSuave
                        )
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("Acepto la ")
                            withStyle(
                                SpanStyle(
                                    color = estado.progreso,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("política de privacidad")
                            }
                        },
                        // Sin color explícito el texto cae a negro y desaparece
                        // sobre el fondo oscuro.
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        modifier = Modifier.clickable { abrirPrivacidad(localContext) }
                    )
                }

                Spacer(Modifier.height(24.dp))

                if (isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = estado.progreso
                        )
                    }
                } else {
                    BotonPrimario(
                        texto = "Crear cuenta",
                        habilitado = puedeRegistrar,
                        onClick = {
                            registerViewModel.doRegister(
                                context = localContext,
                                onRegisterSuccess = { mensaje ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = mensaje,
                                            duration = SnackbarDuration.Short
                                        )
                                        onRegisterSuccess()
                                    }
                                }
                            )
                        }
                    )
                }

                error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = estado.error
                    )
                }

                Spacer(Modifier.height(28.dp))
                Filete()
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "¿Ya tienes una cuenta en este dispositivo?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = estado.textoSuave,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                BotonSecundario(texto = "Entrar", onClick = onNavigateToLogin)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

private fun abrirPrivacidad(context: Context) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(URL_PRIVACIDAD)))
}
