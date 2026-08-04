package com.vatodev.practicapro.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vatodev.practicapro.components.general.ChangePasswordSection
import com.vatodev.practicapro.components.general.NoteCard
import com.vatodev.practicapro.components.modals.SettingsModalContent
import com.vatodev.practicapro.network.BackendGate
import com.vatodev.practicapro.rooms.entitys.Note
import com.vatodev.practicapro.viewmodel.NotesViewModel
import com.vatodev.practicapro.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    userViewModel: UserViewModel = viewModel(),
    notesViewModel: NotesViewModel = viewModel()
) {
    val context = LocalContext.current

    // Controla la visibilidad del modal de "Settings"
    var showSettings by remember { mutableStateOf(false) }

    // Cargamos la información al iniciar la pantalla
    LaunchedEffect(Unit) {
        userViewModel.loadUserProfileFromRoom(context)
        userViewModel.loadTokenFromRoom(context)
        notesViewModel.loadNotes(context)
    }

    // Observamos el perfil y las notas
    val profile = userViewModel.userProfile.value
    val notes = notesViewModel.note.value

    // Scaffold con la barra superior y contenido
    Scaffold(
        topBar = {
            // Barra superior centrada (Material3)
            CenterAlignedTopAppBar(
                title = { Text("Perfil de Usuario") },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar circular (ejemplo con icono)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = Color.Gray,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre y correo
            if (profile != null) {
                Text(
                    text = profile.nombre,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text("Información del usuario no disponible")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título de secciones (Notas)
            Text(text = "Notas del usuario", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Lista de notas
            if (notes.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(notes) { note ->
                        NoteCard(note = note)
                    }
                }
            } else {
                Text("No hay notas")
            }
        }
    }

    if (showSettings) {
        ModalBottomSheet(
            onDismissRequest = { showSettings = false }
        ) {
            SettingsModalContent(onClose = { showSettings = false }) {
                if (BackendGate.isEnabled) {
                    ChangePasswordSection()
                }
            }
        }
    }
}
