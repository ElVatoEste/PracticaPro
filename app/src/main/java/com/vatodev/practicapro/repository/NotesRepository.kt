package com.vatodev.practicapro.repository

import android.content.Context
import android.util.Log
import com.vatodev.practicapro.entitys.ApiNote
import com.vatodev.practicapro.model.CreateNoteRequest
import com.vatodev.practicapro.model.CreateOfflineNoteRequest
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
    suspend fun getNotes(context: Context): Result<List<Note>> {
        return fetchAndSaveNotes(context) // Sincroniza con el servidor
            .mapCatching {
                val localNotes = getLocalNotes(context).getOrThrow()
                Log.d("GetNotes", "Notas después de sincronización: $localNotes")
                localNotes
            }.recoverCatching {
                val localNotes = getLocalNotes(context).getOrThrow()
                Log.d("GetNotes", "Notas obtenidas solo de Room tras error: $localNotes")
                localNotes
            }
    }

    private suspend fun fetchAndSaveNotes(context: Context): Result<Int> {
        return runCatching {
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
            if (isNetworkAvailable) {
                val notesFromApi = notesService.getNotes()
                Log.d("FetchNotes", "Notas obtenidas del servicio: $notesFromApi")

                val notesForRoom = notesFromApi.map { it.toRoomEntity(context) }
                saveNotesLocally(context, notesForRoom)

                Log.d("SaveNotes", "Notas guardadas en Room: $notesForRoom")
            } else {
                throw Exception("No hay conexión a Internet para obtener las notas del servicio.")
            }
        }.recoverCatching { throwable ->
            throw Exception("Error al obtener y guardar las notas: ${throwable.message}")
        }
    }


    private suspend fun getLocalNotes(context: Context): Result<List<Note>> {
        return runCatching {
            val database = DatabaseProvider.getDatabase(context)
            val noteDao = database.noteDao()
            val localNotes = noteDao.getAllNotes()

            Log.d("GetLocalNotes", "Notas obtenidas de Room: $localNotes")
            localNotes
        }.recoverCatching { throwable ->
            throw Exception("Error al leer las notas locales: ${throwable.message}")
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
            val userDao = database.userDao()

            // Obtener el ID del usuario actual
            val userId = userDao.getCurrentUserId()

            // Crear el objeto CreateOfflineNoteRequest con idUsuario
            val offlineRequest = CreateOfflineNoteRequest(
                idUsuario = userId,
                idMateria = request.idMateria,
                puntaje = request.puntaje
            )

            // Serializar la solicitud pendiente
            val json = Json { ignoreUnknownKeys = true }
            val requestJson = json.encodeToString(offlineRequest)

            // Crear y guardar la solicitud pendiente
            val pendingRequest = PendingRequest(
                endpoint = endpoint,
                payload = requestJson,
                method = "POST",
                userId = userId
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

        if (pendingRequests.isEmpty()) {
            Log.d("NotesRepository", "No hay peticiones pendientes para procesar.")
            return
        }

        for (request in pendingRequests) {
            try {
                // Decodificar el payload para obtener la información de CreateOfflineNoteRequest
                val payload = Json.decodeFromString<CreateOfflineNoteRequest>(request.payload)
                Log.d("NotesRepository", "Petición procesada: ${request.id}")

                // Llamar al endpoint con la solicitud decodificada
                notesService.createOfflineNote(payload)

                Log.d("NotesRepository", "Petición procesada exitosamente: ${request.id}")
            } catch (e: Exception) {
                // Log del error, pero la petición se elimina de todos modos
                Log.e("NotesRepository", "Error al procesar la petición ${request.id}: ${e.message}", e)
            } finally {
                // Eliminar la solicitud pendiente, independientemente del resultado
                try {
                    pendingRequestDao.deleteRequestById(request.id)
                    Log.d("NotesRepository", "Petición eliminada de la tabla: ${request.id}")
                } catch (deleteException: Exception) {
                    Log.e("NotesRepository", "Error al eliminar la petición ${request.id}: ${deleteException.message}", deleteException)
                }
            }
        }
    }
}
