package com.vatodev.practicapro.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vatodev.practicapro.model.CreateNoteRequest
import com.vatodev.practicapro.repository.NotesRepository
import com.vatodev.practicapro.rooms.entitys.Note
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {

    val note = mutableStateOf<List<Note>>(emptyList())

    private val successMessage = mutableStateOf<String?>(null)
    private val errorMessage = mutableStateOf<String?>(null)

    fun loadNotes(context: Context) {
        viewModelScope.launch {
            NotesRepository.getNotes(context).fold(
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
