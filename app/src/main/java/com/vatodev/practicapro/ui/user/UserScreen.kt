package com.vatodev.practicapro.ui.user

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vatodev.practicapro.components.general.ChangePasswordSection
import com.vatodev.practicapro.components.general.Etiqueta
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.NoteCard
import com.vatodev.practicapro.components.general.Resumen
import com.vatodev.practicapro.components.modals.SettingsModalContent
import com.vatodev.practicapro.network.BackendGate
import com.vatodev.practicapro.ui.theme.LocalEstado
import com.vatodev.practicapro.viewmodel.NotesViewModel
import com.vatodev.practicapro.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    userViewModel: UserViewModel = viewModel(),
    notesViewModel: NotesViewModel = viewModel()
) {
    val context = LocalContext.current
    var mostrarAjustes by remember { mutableStateOf(false) }
    val estado = LocalEstado.current

    LaunchedEffect(Unit) {
        userViewModel.loadUserProfileFromRoom(context)
        userViewModel.loadTokenFromRoom(context)
        notesViewModel.loadNotes(context)
    }

    val perfil = userViewModel.userProfile.value
    val notas = notesViewModel.note.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Etiqueta("Perfil")
            IconButton(onClick = { mostrarAjustes = true }) {
                Icon(Icons.Default.Settings, "Ajustes", tint = estado.textoSuave)
            }
        }

        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(estado.elevado),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = estado.textoSuave,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.size(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = perfil?.nombre ?: "Sin perfil",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = perfil?.email ?: "Registro local",
                    style = MaterialTheme.typography.bodyMedium,
                    color = estado.textoSuave
                )
            }
        }

        Spacer(Modifier.height(26.dp))
        Resumen(
            listOf(
                "Evaluaciones" to notas.size.toString(),
                "Promedio" to (notas.map { it.score }.average()
                    .takeIf { !it.isNaN() }?.toInt()?.toString() ?: "—"),
                "Mejor" to (notas.maxOfOrNull { it.score }?.toString() ?: "—")
            )
        )

        Spacer(Modifier.height(28.dp))
        Etiqueta("Historial")
        Spacer(Modifier.height(12.dp))

        if (notas.isEmpty()) {
            Filete()
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Aún no has completado ninguna evaluación.",
                style = MaterialTheme.typography.bodyMedium,
                color = estado.textoSuave
            )
            Spacer(Modifier.height(16.dp))
            Filete()
        } else {
            notas.sortedByDescending { it.dateMillis }.forEach { NoteCard(it) }
            Filete()
        }

        Spacer(Modifier.height(28.dp))
    }

    if (mostrarAjustes) {
        ModalBottomSheet(
            onDismissRequest = { mostrarAjustes = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            SettingsModalContent(onClose = { mostrarAjustes = false }) {
                if (BackendGate.isEnabled) {
                    ChangePasswordSection()
                }
            }
        }
    }
}
