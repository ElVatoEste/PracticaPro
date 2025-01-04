package com.example.practicapro.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.practicapro.viewmodel.VerificationViewModel

@Composable
fun VerificationCodeModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: VerificationViewModel,
    email: String,
    context: android.content.Context,
    onConfirmSuccess: () -> Unit
) {
    if (isVisible) {

        LaunchedEffect(Unit) {
            viewModel.onEmailChange(email)
        }

        AlertDialog(
            onDismissRequest = onDismiss,
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
                                viewModel.confirmEmail(context)
                            }
                        ),
                        singleLine = true
                    )

                    if (viewModel.error.value != null) {
                        Text(
                            text = viewModel.error.value!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmEmail(context)
                        if (viewModel.isSuccess.value) {
                            onConfirmSuccess()
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}
