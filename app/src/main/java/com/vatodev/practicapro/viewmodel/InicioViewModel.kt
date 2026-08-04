package com.vatodev.practicapro.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vatodev.practicapro.model.MODULOS
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Estado de un módulo en el índice: cuántos intentos lleva y su mejor nota. */
data class EstadoModulo(
    val subjectId: Int,
    val intentos: Int,
    val mejorNota: Int?
)

data class ResumenInicio(
    val modulos: Int = MODULOS.size,
    val intentosUsados: Int = 0,
    val intentosTotales: Int = MODULOS.sumOf { it.maxIntentos },
    val promedio: Int? = null,
    val porModulo: Map<Int, EstadoModulo> = emptyMap()
)

/**
 * Cifras del índice, calculadas desde Room. Todo local: no consulta la red.
 */
class InicioViewModel : ViewModel() {

    private val _resumen = mutableStateOf(ResumenInicio())
    val resumen: State<ResumenInicio> = _resumen

    fun cargar(context: Context) {
        viewModelScope.launch {
            val notas = withContext(Dispatchers.IO) {
                DatabaseProvider.getDatabase(context).noteDao().getAllNotes()
            }

            val porModulo = MODULOS.associate { modulo ->
                val delModulo = notas.filter { it.subjectId == modulo.subjectId }
                modulo.subjectId to EstadoModulo(
                    subjectId = modulo.subjectId,
                    intentos = delModulo.size,
                    mejorNota = delModulo.maxOfOrNull { it.score }
                )
            }

            _resumen.value = ResumenInicio(
                intentosUsados = notas.size,
                promedio = notas.map { it.score }.average().takeIf { !it.isNaN() }?.toInt(),
                porModulo = porModulo
            )
        }
    }
}
