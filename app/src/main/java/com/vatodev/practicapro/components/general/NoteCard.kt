package com.vatodev.practicapro.components.general

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.vatodev.practicapro.ui.theme.LocalEstado
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vatodev.practicapro.rooms.entitys.Note

/** Verde a partir de 80, morado entre 60 y 79, coral por debajo. */
@Composable
fun getScoreColor(score: Int): Color {
    val estado = LocalEstado.current
    return when {
        score >= 80 -> estado.progreso
        score in 60..79 -> estado.logro
        else -> estado.error
    }
}


@Composable
fun NoteCard(note: Note) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.subjectName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Fecha: ${note.date}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Intento: ${note.attempt}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Área derecha: Puntaje en grande, centrado y con color según su valor
            Box(
                modifier = Modifier.wrapContentWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${note.score}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = getScoreColor(note.score)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoteCard() {
    val sampleNote = Note(
        subjectName = "Matemáticas",
        score = 85,
        date = "2024-01-20",
        id = 1,
        attempt = 1,
        subjectId = 1
    )
    NoteCard(note = sampleNote)
}
