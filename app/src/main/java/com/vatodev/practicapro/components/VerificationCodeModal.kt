package com.vatodev.practicapro.components

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
import com.vatodev.practicapro.viewmodel.VerificationViewModel

@Composable
fun VerificationCodeModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: VerificationViewModel,
    email: String,
    context: android.content.Context,
    onConfirmSuccess: () -> Unit
) {
    val greenColor = Color(0xFF7DBB00)

    if (isVisible) {

        LaunchedEffect(Unit) {
            viewModel.onEmailChange(email)
        }

        AlertDialog(
            onDismissRequest = {
                viewModel.clearMessages()
                onDismiss()
            },
            title = { Text("Confirmación de Correo") },
            text = {
                Column {
                    Text("Ingrese el código de verificación enviado a su correo.")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.code.value,
                        onValueChange = { viewModel.onCodeChange(it) },
                        label = { Text("Código de Verificación") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.clearMessages()
                                viewModel.confirmEmail(
                                    onSuccess = { onConfirmSuccess() },
                                    onError = { message -> viewModel.onErrorChange(message) }
                                )
                            }
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (viewModel.error.value != null) {
                        Text(
                            text = viewModel.error.value!!,
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

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            viewModel.clearMessages()
                            viewModel.resendVerificationEmail(
                                context,
                                onSuccess = { message -> viewModel.onSuccessMessageChange(message) },
                                onError = { message -> viewModel.onErrorChange(message) }
                            )
                        }
                    ) {
                        Text(
                            text = "¿No recibiste el correo? Reenviar código",
                            color = greenColor,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearMessages()
                        viewModel.confirmEmail(
                            onSuccess = { onConfirmSuccess() },
                            onError = { message -> viewModel.onErrorChange(message) }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = greenColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.clearMessages()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = greenColor
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
