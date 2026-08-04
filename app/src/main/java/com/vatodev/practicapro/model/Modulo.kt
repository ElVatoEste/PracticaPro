package com.vatodev.practicapro.model

import androidx.annotation.DrawableRes
import com.vatodev.practicapro.R
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.rooms.entitys.Materia

/**
 * Un módulo de estudio.
 *
 * [subjectId] es la clave de la tabla `materia` y la que usan las notas para
 * contar intentos. [maxIntentos] debe coincidir con el umbral de
 * `NoteDao.hasReachedMaxAttempts`.
 */
data class Modulo(
    val indice: String,
    val nombre: String,
    val descripcion: String,
    val subjectId: Int,
    val ruta: String,
    @DrawableRes val imagen: Int,
    val maxIntentos: Int = 2
)

/**
 * Catálogo de módulos. Antes vivía duplicado dentro de MainScreen, con la
 * navegación resuelta comparando el nombre visible del módulo.
 */
val MODULOS = listOf(
    Modulo(
        indice = "01",
        nombre = "Asepsia y antisepsia",
        descripcion = "Barreras, lavado de manos y campo estéril.",
        subjectId = 1,
        ruta = Routes.TECNICAS,
        imagen = R.drawable.ic_asepsia
    ),
    Modulo(
        indice = "02",
        nombre = "Procedimientos",
        descripcion = "Atención al paciente paso a paso.",
        subjectId = 2,
        ruta = Routes.PROCEDIMIENTOS,
        imagen = R.drawable.ic_procedures
    ),
    Modulo(
        indice = "03",
        nombre = "Medicamentos",
        descripcion = "Vías, cálculo de dosis y registro.",
        subjectId = 3,
        ruta = Routes.ADMINISTRACION,
        imagen = R.drawable.ic_medicines
    ),
    Modulo(
        indice = "04",
        nombre = "Urgencias",
        descripcion = "Manejo inicial de urgencias médicas.",
        subjectId = 4,
        ruta = Routes.URGENCIAS,
        imagen = R.drawable.ic_emergency
    )
)

/** Segunda evaluación de procedimientos, en formato verdadero/falso. */
const val SUBJECT_PROCEDIMIENTOS_VF = 5

/**
 * Catálogo de la tabla `materia`. Deriva de [MODULOS] para que un módulo
 * nuevo no exija recordar sembrar su materia.
 */
val MATERIAS: List<Materia> = MODULOS.map { Materia(it.subjectId, it.nombre.uppercase()) } +
    Materia(SUBJECT_PROCEDIMIENTOS_VF, "PROCEDIMIENTOS2")
