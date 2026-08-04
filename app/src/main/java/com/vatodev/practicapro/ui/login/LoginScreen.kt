package com.vatodev.practicapro.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.components.general.BotonPrimario
import com.vatodev.practicapro.components.general.BotonSecundario
import com.vatodev.practicapro.components.general.Etiqueta
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.PasswordTextField
import com.vatodev.practicapro.repository.AuthRepository
import com.vatodev.practicapro.rooms.entitys.User
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.LocalEstado
import kotlinx.coroutines.launch

/**
 * Acceso a una cuenta local.
 *
 * Muestra las cuentas del dispositivo en lugar de pedir el correo a ciegas:
 * son pocas y el usuario las reconoce por nombre.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()
    val estado = LocalEstado.current

    var cuentas by remember { mutableStateOf(emptyList<User>()) }
    var elegida by remember { mutableStateOf<User?>(null) }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        cuentas = AuthRepository.cuentas(contexto)
        if (cuentas.size == 1) elegida = cuentas.first()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(72.dp))
        Text(
            text = "PRACTICAPRO",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 20.sp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
        ) {
            Etiqueta(if (elegida == null) "Elige tu cuenta" else "Entrar")
            Spacer(Modifier.height(16.dp))

            val cuenta = elegida
            if (cuenta == null) {
                cuentas.forEach { c ->
                    Filete()
                    FilaCuenta(c) {
                        elegida = c
                        error = null
                    }
                }
                Filete()
            } else {
                Filete()
                FilaCuenta(cuenta, seleccionada = true) {}
                Filete()

                Spacer(Modifier.height(20.dp))

                if (cuenta.sinContrasena) {
                    Text(
                        text = "Esta cuenta se creó antes del acceso local y no tiene contraseña. " +
                            "Entra y ponle una desde tu perfil.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = estado.textoSuave
                    )
                    Spacer(Modifier.height(20.dp))
                } else {
                    PasswordTextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                }

                error?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = estado.error
                    )
                    Spacer(Modifier.height(12.dp))
                }

                BotonPrimario(
                    texto = "Entrar",
                    habilitado = cuenta.sinContrasena || password.isNotEmpty(),
                    onClick = {
                        alcance.launch {
                            AuthRepository.iniciarSesion(contexto, cuenta.email, password)
                                .onSuccess { onLoginSuccess() }
                                .onFailure { error = it.message ?: "No se pudo entrar." }
                        }
                    }
                )

                if (cuentas.size > 1) {
                    Spacer(Modifier.height(10.dp))
                    BotonSecundario(
                        texto = "Cambiar de cuenta",
                        onClick = { elegida = null; password = ""; error = null }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            Filete()
            Spacer(Modifier.height(20.dp))

            Text(
                text = "¿Otra persona usa este dispositivo?",
                style = MaterialTheme.typography.bodyMedium,
                color = estado.textoSuave,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            BotonSecundario(texto = "Crear otra cuenta", onClick = onNavigateToRegister)
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun FilaCuenta(cuenta: User, seleccionada: Boolean = false, onClick: () -> Unit) {
    val estado = LocalEstado.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !seleccionada, onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (seleccionada) estado.progreso else estado.elevado),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cuenta.username.trim().take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = if (seleccionada) {
                    MaterialTheme.colorScheme.background
                } else {
                    estado.textoSuave
                }
            )
        }
        Spacer(Modifier.size(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = cuenta.username,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = cuenta.email,
                style = Dato.copy(fontSize = 12.sp),
                color = estado.textoSuave
            )
        }
    }
}
