package com.example.practicapro.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicapro.model.CreateNoteRequest
import com.example.practicapro.repository.NotesRepository
import com.example.practicapro.rooms.entitys.Note
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {

    private val note = mutableStateOf<List<Note>>(emptyList())

    private val successMessage = mutableStateOf<String?>(null)
    private val errorMessage = mutableStateOf<String?>(null)

    fun loadNotes(context: Context, quizName: String) {
        viewModelScope.launch {
            NotesRepository.getNotes(context, quizName).fold(
                onSuccess = { notesList ->
                    note.value = notesList
                },
                onFailure = { throwable ->
                    errorMessage.value = throwable.message
                }
            )
        }
    }

    fun addNote(context: Context, idMateria: Int, puntaje: Int) {
        viewModelScope.launch {
            val request = CreateNoteRequest(idMateria = idMateria, puntaje = puntaje)
            NotesRepository.createNote(context, request).fold(
                onSuccess = {
                    successMessage.value = "Nota creada exitosamente."
                },
                onFailure = { throwable ->
                    errorMessage.value = throwable.message
                }
            )
        }
    }
}
