package com.example.practicapro.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://localhost:3000/" // Cambiar por la URL de tu API

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Nivel de logs: Body muestra detalles completos
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Agregar logs
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // URL base de la API
        .client(okHttpClient) // Cliente HTTP configurado
        .addConverterFactory(GsonConverterFactory.create()) // Conversor para JSON
        .build()
}
