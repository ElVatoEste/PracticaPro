package com.vatodev.practicapro.components.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import android.content.Context
import com.vatodev.practicapro.viewmodel.ResetPasswordViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordModal(
    isVisible: Boolean,
    viewModel: ResetPasswordViewModel,
    onDismiss: () -> Unit,
    context: Context,
    onResetSuccess: (String) -> Unit
) {
    val greenColor = Color(0xFF7DBB00)
    // Local variable to handle error if the field is empty
    var localError by remember { mutableStateOf<String?>(null) }
    // Cooldown flag to prevent sending too many emails
    var isCooldown by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (isVisible) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearMessages()
                localError = null
                onDismiss()
            },
            title = { Text("Restablecer Contraseña") },
            text = {
                Column {
                    Text("Ingrese su correo electrónico para restablecer su contraseña.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.email.value,
                        onValueChange = {
                            viewModel.onEmailChange(it)
                            viewModel.clearMessages()
                            localError = null
                        },
                        label = { Text("Correo Electrónico") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val errorMessage = localError ?: viewModel.error.value
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else if (viewModel.successMessage.value.isNotEmpty()) {
                        Text(
                            text = viewModel.successMessage.value,
                            color = greenColor,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (viewModel.email.value.isBlank()) {
                            localError = "Por favor, ingrese un correo electrónico válido."
                        } else if (isCooldown) {
                            localError = "Por favor, espere antes de enviar otro correo."
                        } else {
                            viewModel.sendResetCode(
                                context = context,
                                onSuccess = { message ->
                                    onResetSuccess(viewModel.email.value)
                                },
                                onError = {
                                    // Handle error if needed
                                }
                            )
                            // Start cooldown for 60 seconds
                            isCooldown = true
                            coroutineScope.launch {
                                delay(60000L)
                                isCooldown = false
                            }
                        }
                    },
                    enabled = !viewModel.isLoading.value && !isCooldown,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = greenColor,
                        contentColor = Color.White
                    )
                ) {
                    if (viewModel.isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Enviar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.clearMessages()
                        localError = null
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = greenColor
                    )
                ) {
                    Text("Cancelar", textDecoration = TextDecoration.Underline)
                }
            }
        )
    }
}
