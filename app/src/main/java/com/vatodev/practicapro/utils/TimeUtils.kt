package com.vatodev.practicapro.utils

// Función de extensión para convertir días a milisegundos
fun Int.days(): Long = this * 24 * 60 * 60 * 1000L

// Función de extensión para convertir horas a milisegundos
fun Int.hours(): Long = this * 60 * 60 * 1000L

// Función de extensión para convertir minutos a milisegundos
fun Int.minutes(): Long = this * 60 * 1000L

fun Int.seconds(): Long = this * 100L
