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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vatodev.practicapro.rooms.entitys.Note

fun getScoreColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF2E7D32) // Verde 600
        score in 60..79 -> Color(0xFFF9A825) // Amarillo 800
        else -> Color(0xFFC62828) // Rojo 700
    }
}


@Composable
fun NoteCard(note: Note) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
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
