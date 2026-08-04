package com.vatodev.practicapro.components.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vatodev.practicapro.rooms.entitys.Note
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.LocalEstado
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

/** Fila del historial. La puntuación manda a la derecha, en cifra tabular. */
@Composable
fun NoteCard(note: Note) {
    val estado = LocalEstado.current

    Column {
        Filete()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = note.subjectName,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Intento ${note.attempt} · ${fecha(note)}",
                    style = Dato.copy(fontSize = 11.sp),
                    color = estado.textoSuave
                )
            }
            Text(
                text = "${note.score}",
                style = Dato.copy(fontSize = 30.sp, letterSpacing = (-1).sp),
                color = getScoreColor(note.score)
            )
        }
    }
}

/** `dateMillis` es 0 en las notas migradas que no traían fecha numérica. */
private fun fecha(note: Note): String = if (note.dateMillis > 0) {
    SimpleDateFormat("dd MMM yyyy", Locale("es")).format(Date(note.dateMillis))
} else {
    "Sin fecha"
}
