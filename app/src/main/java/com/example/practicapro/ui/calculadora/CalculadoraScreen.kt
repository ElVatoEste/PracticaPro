package com.example.practicapro.ui.calculadora

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import com.example.practicapro.rooms.entitys.Note
import com.example.practicapro.rooms.entitys.PendingRequest
import kotlinx.coroutines.launch

@Composable
fun CalculadoraScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estados para mostrar las notas y peticiones pendientes
    var notesList by remember { mutableStateOf<List<Note>>(emptyList()) }
    var pendingRequestsList by remember { mutableStateOf<List<PendingRequest>>(emptyList()) }

    // Estado para mostrar el resultado
    var result by remember { mutableStateOf("Calculadora") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = result)

            // Botón para cargar todas las notas
            Button(
                onClick = {
                    scope.launch {
                        val database = DatabaseProvider.getDatabase(context)
                        notesList = database.noteDao().getAllNotes()
                        result = "Notas cargadas: ${notesList.size}"
                        Log.d("CalculadoraScreen", "Notas: $notesList")
                    }
                }
            ) {
                Text(text = "Cargar Notas")
            }

            // Mostrar notas cargadas
            notesList.forEach { note ->
                Text(text = "Nota: ${note.subjectName}, Puntaje: ${note.score}, Intento: ${note.attempt}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para cargar peticiones pendientes
            Button(
                onClick = {
                    scope.launch {
                        val database = DatabaseProvider.getDatabase(context)
                        pendingRequestsList = database.pendingRequestDao().getAllRequests()
                        result = "Peticiones pendientes: ${pendingRequestsList.size}"
                        Log.d("CalculadoraScreen", "Peticiones Pendientes: $pendingRequestsList")
                    }
                }
            ) {
                Text(text = "Cargar Peticiones Pendientes")
            }

            // Mostrar peticiones pendientes
            pendingRequestsList.forEach { request ->
                Text(text = "Petición: ${request.endpoint}, Payload: ${request.payload}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculadoraScreenPreview() {
    CalculadoraScreen()
}
