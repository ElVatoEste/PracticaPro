package com.vatodev.practicapro.repository

import android.content.Context
import android.util.Log
import com.vatodev.practicapro.entitys.ApiNote
import com.vatodev.practicapro.model.CreateNoteRequest
import com.vatodev.practicapro.network.ApiClient
import com.vatodev.practicapro.network.NetworkObserver
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.Note
import com.vatodev.practicapro.rooms.entitys.PendingRequest
import com.vatodev.practicapro.service.NotesService
import com.vatodev.practicapro.utils.toRoomEntity
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object NotesRepository {

    private val notesService: NotesService by lazy {
        ApiClient.retrofit.create(NotesService::class.java)
    }

    // ✅ Obtener notas y guardarlas en Room
    suspend fun getNotes(context: Context, quizName: String): Result<List<Note>> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (isNetworkAvailable) {
                val notesFromApi = notesService.getNotes()
                val notesForRoom = notesFromApi.map { it.toRoomEntity(context) }
                saveNotesLocally(context, notesForRoom)
                notesForRoom
            } else {
                // Obtener las notas almacenadas localmente en Room
                val database = DatabaseProvider.getDatabase(context)
                val noteDao = database.noteDao()
                noteDao.getNotes(quizName)
            }
        }.recoverCatching { throwable ->
            throw Exception("Error al obtener las notas: ${throwable.message}")
        }
    }

    // Crear una nueva nota
    suspend fun createNote(context: Context, request: CreateNoteRequest): Result<ApiNote> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (isNetworkAvailable) {
                val note = notesService.createNote(request)
                val localNote = note.toRoomEntity(context)

                // Guardar la nota en Room
                saveNoteLocally(context, localNote)
                Log.d("NotesRepository", "Nota creada y guardada en Room: $localNote")
                note
            } else {
                savePendingRequest(context, "notas", request)
                throw Exception("Nota guardada localmente. Se enviará cuando haya conexión.")
            }
        }.recoverCatching { throwable ->
            throw Exception("Error al crear la nota: ${throwable.message}")
        }
    }

    // ✅ Guardar una lista de notas en Room
    private suspend fun saveNotesLocally(context: Context, notes: List<Note>) {
        val database = DatabaseProvider.getDatabase(context)
        val noteDao = database.noteDao()
        noteDao.insertNotes(notes)
    }

    private suspend fun saveNoteLocally(context: Context, note: Note) {
        val database = DatabaseProvider.getDatabase(context)
        val noteDao = database.noteDao()

        noteDao.insertNote(note)
    }

    private suspend fun savePendingRequest(context: Context, endpoint: String, request: CreateNoteRequest) {
        try {
            val database = DatabaseProvider.getDatabase(context)
            val pendingRequestDao = database.pendingRequestDao()

            // ✅ Usa Json con configuración personalizada
            val json = Json { ignoreUnknownKeys = true }
            val requestJson = json.encodeToString(request)

            val pendingRequest = PendingRequest(
                endpoint = endpoint,
                payload = requestJson,
                method = "POST"
            )

            pendingRequestDao.insertRequest(pendingRequest)
            Log.d("NotesRepository", "Petición pendiente guardada: $pendingRequest")
        } catch (e: Exception) {
            Log.e("NotesRepository", "Error al guardar la petición pendiente: ${e.message}", e)
        }
    }


    // ✅ Procesar las peticiones pendientes
    suspend fun processPendingRequests(context: Context) {
        val database = DatabaseProvider.getDatabase(context)
        val pendingRequestDao = database.pendingRequestDao()
        val pendingRequests = pendingRequestDao.getAllRequests()

        for (request in pendingRequests) {
            try {
                val payload = Json.decodeFromString<CreateNoteRequest>(request.payload)
                notesService.createNote(payload)
                pendingRequestDao.deleteRequestById(request.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
