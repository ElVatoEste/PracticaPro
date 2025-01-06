package com.example.practicapro.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://practica-pro-back.vercel.app/"
    private const val LOCAL_URL = "http://192.168.0.3:3000/"
    private const val IS_LOCAL = true
    private const val TAG = "ApiClient"

    // Interceptor de logs
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor personalizado para loggear cuerpo de request y response
    private val bodyInterceptor = Interceptor { chain ->
        val request = chain.request()

        // Log del cuerpo del request
        Log.d(TAG, "➡️ Enviando Request: ${request.method} ${request.url}")
        request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            Log.d(TAG, "📦 Request Body: ${buffer.readUtf8()}")
        }

        // Procesar la respuesta
        val response = chain.proceed(request)

        // Log del cuerpo de la response
        Log.d(TAG, "⬅️ Recibiendo Response: ${response.code} ${response.message}")
        response.body?.let { responseBody ->
            val responseBodyString = responseBody.string()
            Log.d(TAG, "📦 Response Body: $responseBodyString")

            // Rehacer el cuerpo de la respuesta para que esté disponible para la aplicación
            response.newBuilder()
                .body(okhttp3.ResponseBody.create(responseBody.contentType(), responseBodyString))
                .build()
        } ?: response
    }

    // Cliente HTTP
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Logs de Retrofit
        .addInterceptor(bodyInterceptor) // Interceptor personalizado
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Selección de URL base
    private val baseUrl = if (IS_LOCAL) LOCAL_URL else BASE_URL

    // Cliente Retrofit
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
