package com.vatodev.practicapro.repository

import android.content.Context
import android.util.Log
import com.vatodev.practicapro.model.CreateNoteRequest
import com.vatodev.practicapro.model.CreateOfflineNoteRequest
import com.vatodev.practicapro.network.ApiClient
import com.vatodev.practicapro.network.BackendGate
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.Note
import com.vatodev.practicapro.service.NotesService
import com.vatodev.practicapro.utils.toRoomEntity

object NotesRepository {

    private const val TAG = "NotesRepository"

    private val notesService: NotesService by lazy {
        ApiClient.retrofit.create(NotesService::class.java)
    }

    suspend fun getNotes(context: Context): Result<List<Note>> {
        return fetchAndSaveNotes(context)
            .mapCatching { getLocalNotes(context).getOrThrow() }
            .recoverCatching { getLocalNotes(context).getOrThrow() }
    }

    private suspend fun fetchAndSaveNotes(context: Context): Result<Unit> = runCatching {
        if (!BackendGate.isReachable()) return@runCatching

        val remotas = notesService.getNotes().map { it.toRoomEntity(context) }
        DatabaseProvider.getDatabase(context).noteDao().insertNotes(remotas)
        Log.d(TAG, "Notas sincronizadas desde el servidor: ${remotas.size}")
    }

    private suspend fun getLocalNotes(context: Context): Result<List<Note>> = runCatching {
        DatabaseProvider.getDatabase(context).noteDao().getAllNotes(SesionRepository.idParaConsultas(context))
    }

    /**
     * Escribe en Room de forma incondicional y sincroniza después. Un fallo
     * de red no puede propagarse como fallo de la operación.
     */
    suspend fun createNote(context: Context, request: CreateNoteRequest): Result<Note> = runCatching {
        val local = buildLocalNote(context, request)
        val dao = DatabaseProvider.getDatabase(context).noteDao()
        dao.insertNote(local)
        Log.d(TAG, "Nota guardada en Room: $local")

        if (BackendGate.isReachable()) {
            runCatching { notesService.createNote(request) }
                .onSuccess { dao.insertNote(local.copy(remoteId = it.id, synced = true)) }
                .onFailure { Log.w(TAG, "Queda pendiente de sincronizar: ${it.message}") }
        }

        local
    }

    /** Id negativo y decreciente, para no colisionar con los del servidor. */
    private suspend fun buildLocalNote(context: Context, request: CreateNoteRequest): Note {
        val database = DatabaseProvider.getDatabase(context)
        val noteDao = database.noteDao()
        val materia = database.materiaDao().getMateriaById(request.idMateria)
        val usuario = SesionRepository.idParaConsultas(context)

        return Note(
            id = minOf(noteDao.minId() ?: 0, 0) - 1,
            userId = usuario,
            synced = false,
            score = request.puntaje,
            attempt = noteDao.countBySubject(request.idMateria, usuario) + 1,
            dateMillis = System.currentTimeMillis(),
            subjectId = request.idMateria,
            subjectName = materia?.name ?: "Desconocido"
        )
    }

    /**
     * Sube las notas que el servidor todavía no ha confirmado.
     *
     * Sustituye a la tabla `pending_requests`: cada nota lleva su propio
     * `synced`, así que no hay una cola paralela que mantener en sincronía ni
     * que crezca sin techo mientras el backend esté caído.
     */
    suspend fun sincronizarPendientes(context: Context) {
        if (!BackendGate.isReachable()) return

        val database = DatabaseProvider.getDatabase(context)
        val dao = database.noteDao()
        val userId = SesionRepository.idParaConsultas(context)
        val pendientes = dao.getUnsynced(userId)

        if (pendientes.isEmpty()) return
        Log.d(TAG, "Notas pendientes de subir: ${pendientes.size}")

        pendientes.forEach { nota ->
            runCatching {
                notesService.createOfflineNote(
                    CreateOfflineNoteRequest(
                        idUsuario = userId,
                        idMateria = nota.subjectId,
                        puntaje = nota.score
                    )
                )
            }.onSuccess {
                dao.insertNote(nota.copy(remoteId = it.id, synced = true))
                Log.d(TAG, "Nota ${nota.id} sincronizada como ${it.id}")
            }.onFailure {
                Log.w(TAG, "Nota ${nota.id} sigue pendiente: ${it.message}")
            }
        }
    }
}
