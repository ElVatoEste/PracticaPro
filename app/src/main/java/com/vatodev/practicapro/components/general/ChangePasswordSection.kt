package com.vatodev.practicapro.components.general

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vatodev.practicapro.viewmodel.ChangePasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordSection(viewModel: ChangePasswordViewModel = viewModel()) {
    // Controla la visibilidad del modal
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    // Estados locales para los campos de contraseña
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Botón principal: utiliza el color verde de la app
    Button(
        onClick = { showChangePasswordDialog = true },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DBB00))
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Cambiar Contraseña"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Cambiar Contraseña")
    }

    // Extraemos el successMessage del ViewModel en una variable local
    val successMsg = viewModel.successMessage

    // Cuando successMsg cambia a no vacío, esperamos 2 segundos y cerramos el modal
    LaunchedEffect(successMsg) {
        if (successMsg.isNotEmpty()) {
            kotlinx.coroutines.delay(2000)
            showChangePasswordDialog = false
        }
    }

    if (showChangePasswordDialog) {
        ModalBottomSheet(
            onDismissRequest = { showChangePasswordDialog = false }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cambiar Contraseña",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar mensajes de error o éxito
                if (viewModel.errorMessage.isNotEmpty()) {
                    Text(
                        text = viewModel.errorMessage,
                        color = Color(0xFFC62828),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else if (newPassword != confirmPassword && confirmPassword.isNotEmpty()) {
                    // Validación local para que las contraseñas coincidan
                    Text(
                        text = "Las contraseñas nuevas no coinciden",
                        color = Color(0xFFC62828),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (successMsg.isNotEmpty()) {
                    Text(
                        text = successMsg,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Campo: Contraseña actual
                PasswordTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Contraseña actual") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Campo: Nueva contraseña
                PasswordTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contraseña") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Campo: Confirmar nueva contraseña
                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar nueva contraseña") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Botón para enviar: se invoca el ViewModel solo si las contraseñas coinciden
                Button(
                    onClick = {
                        if (newPassword == confirmPassword) {
                            viewModel.changePassword(currentPassword, newPassword)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DBB00))
                ) {
                    Text("Enviar")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
