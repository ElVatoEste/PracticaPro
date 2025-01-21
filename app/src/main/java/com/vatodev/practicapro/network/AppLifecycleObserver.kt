package com.vatodev.practicapro.network

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import java.util.UUID

class AppLifecycleObserver(private val context: Context) : LifecycleObserver {

    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private var currentSessionId: String? = null

    init {
        val storedSessionId = sharedPreferences.getString("current_session_id", null)

        // Generar un nuevo identificador único para la sesión actual
        currentSessionId = UUID.randomUUID().toString()
        sharedPreferences.edit().putString("current_session_id", currentSessionId).apply()

        // Verificar si la sesión actual es nueva
        if (storedSessionId != currentSessionId) {
            Log.d("AppLifecycleObserver", "Nueva instancia detectada. Reiniciando estado de notas.")
            sharedPreferences.edit().putBoolean("notes_loaded", false).apply()
        } else {
            Log.d("AppLifecycleObserver", "La sesión aún es válida.")
        }

        Log.d("AppLifecycleObserver", "Sesión actual iniciada: $currentSessionId")
    }
}
