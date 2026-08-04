package com.vatodev.practicapro.repository

import android.content.Context
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.rooms.entitys.ProgresoTecnica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Dónde se quedó el usuario dentro de cada técnica. */
object ProgresoRepository {

    private suspend fun dao(context: Context) =
        DatabaseProvider.getDatabase(context).progresoTecnicaDao()

    suspend fun guardar(context: Context, tecnica: Tecnica, modulo: String, paso: Int) {
        withContext(Dispatchers.IO) {
            dao(context).guardar(
                ProgresoTecnica(
                    clave = tecnica.clave,
                    userId = SesionRepository.idParaConsultas(context),
                    modulo = modulo,
                    titulo = tecnica.titulo,
                    pasoActual = paso,
                    totalPasos = tecnica.pasos.size,
                    actualizado = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun paso(context: Context, clave: String): Int? =
        withContext(Dispatchers.IO) { dao(context).porClave(clave, SesionRepository.idParaConsultas(context))?.pasoActual }

    suspend fun deModulo(context: Context, modulo: String): Map<String, ProgresoTecnica> =
        withContext(Dispatchers.IO) {
            dao(context).porModulo(modulo, SesionRepository.idParaConsultas(context)).associateBy { it.clave }
        }

    /** Alimenta la tarjeta "continúa donde lo dejaste" del índice. */
    suspend fun ultimaSinTerminar(context: Context): ProgresoTecnica? =
        withContext(Dispatchers.IO) { dao(context).ultimaSinTerminar(SesionRepository.idParaConsultas(context)) }
}
