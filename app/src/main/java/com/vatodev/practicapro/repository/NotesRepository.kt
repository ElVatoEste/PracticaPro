package com.vatodev.practicapro.repository

import android.content.Context
import android.util.Log
import com.vatodev.practicapro.entitys.ApiNote
import com.vatodev.practicapro.model.CreateNoteRequest
import com.vatodev.practicapro.model.CreateOfflineNoteRequest
import com.vatodev.practicapro.network.ApiClient
import com.vatodev.practicapro.network.BackendGate
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.Note
import com.vatodev.practicapro.rooms.entitys.PendingRequest
import com.vatodev.practicapro.service.NotesService
import com.vatodev.practicapro.utils.toRoomEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object NotesRepository {

    private val notesService: NotesService by lazy {
        ApiClient.retrofit.create(NotesService::class.java)
    }

    suspend fun getNotes(context: Context): Result<List<Note>> {
        return fetchAndSaveNotes(context)
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
            if (BackendGate.isReachable()) {
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


    /**
     * Escribe en Room de forma incondicional y sincroniza después. Un fallo
     * de red no puede propagarse como fallo de la operación.
     */
    suspend fun createNote(context: Context, request: CreateNoteRequest): Result<Note> {
        return runCatching {
            val localNote = buildLocalNote(context, request)
            saveNoteLocally(context, localNote)
            Log.d("NotesRepository", "Nota guardada en Room: $localNote")

            if (BackendGate.isReachable()) {
                runCatching { notesService.createNote(request) }
                    .onSuccess { Log.d("NotesRepository", "Nota sincronizada: $it") }
                    .onFailure {
                        Log.w("NotesRepository", "Sincronización fallida, se encola: ${it.message}")
                        savePendingRequest(context, "notas", request)
                    }
            } else {
                savePendingRequest(context, "notas", request)
            }

            localNote
        }
    }

    /** Id negativo y decreciente, para no colisionar con los del servidor. */
    private suspend fun buildLocalNote(context: Context, request: CreateNoteRequest): Note {
        val database = DatabaseProvider.getDatabase(context)
        val noteDao = database.noteDao()
        val materia = database.materiaDao().getMateriaById(request.idMateria)

        val now = System.currentTimeMillis()

        return Note(
            id = minOf(noteDao.minId() ?: 0, 0) - 1,
            score = request.puntaje,
            attempt = noteDao.countBySubject(request.idMateria) + 1,
            date = now.toString(),
            dateMillis = now,
            subjectId = request.idMateria,
            subjectName = materia?.name ?: "Desconocido"
        )
    }

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

            val userId = userDao.getCurrentUserId()

            val offlineRequest = CreateOfflineNoteRequest(
                idUsuario = userId,
                idMateria = request.idMateria,
                puntaje = request.puntaje
            )

            val json = Json { ignoreUnknownKeys = true }
            val requestJson = json.encodeToString(offlineRequest)

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

    suspend fun processPendingRequests(context: Context) {
        val database = DatabaseProvider.getDatabase(context)
        val pendingRequestDao = database.pendingRequestDao()
        val pendingRequests = pendingRequestDao.getAllRequests()

        if (pendingRequests.isEmpty()) {
            Log.d("NotesRepository", "No hay peticiones pendientes para procesar.")
            return
        }

        for (request in pendingRequests) {
            // Un payload ilegible no se arregla reintentando: se descarta.
            val payload = runCatching {
                Json.decodeFromString<CreateOfflineNoteRequest>(request.payload)
            }.getOrElse {
                Log.e("NotesRepository", "Payload ilegible en ${request.id}, se descarta", it)
                pendingRequestDao.deleteRequestById(request.id)
                continue
            }

            runCatching { notesService.createOfflineNote(payload) }
                .onSuccess {
                    pendingRequestDao.deleteRequestById(request.id)
                    Log.d("NotesRepository", "Petición ${request.id} sincronizada y eliminada")
                }
                .onFailure {
                    Log.w("NotesRepository", "Petición ${request.id} sigue pendiente: ${it.message}")
                }
        }
    }
}
