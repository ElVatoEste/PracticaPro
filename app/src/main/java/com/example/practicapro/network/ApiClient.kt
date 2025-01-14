package com.example.practicapro.network

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private var token: String? = null

    // 👉 Token Flow para observar cambios
    private val _tokenFlow = MutableStateFlow<String?>(null)
    val tokenFlow = _tokenFlow.asStateFlow()

    // 👉 Configurar el token
    fun setToken(newToken: String?) {
        _tokenFlow.value = newToken
        token = newToken
        Log.d(TAG, "setToken() - Token configurado: $newToken")
    }

    // Interceptor para agregar el token a las solicitudes
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Log para verificar si el token está presente
        Log.d(TAG, "authInterceptor - Token actual: $token")

        // Agregar el token si está disponible
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
            Log.d(TAG, "authInterceptor - Token agregado al encabezado Authorization")
        }

        val request = requestBuilder.build()
        chain.proceed(request)
    }

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
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(bodyInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val baseUrl = if (IS_LOCAL) LOCAL_URL else BASE_URL

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
