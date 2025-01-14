package com.example.practicapro.rooms.appDatabase

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.practicapro.rooms.entitys.Materia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration() // 👉 Permite migraciones destructivas
                .build()
            INSTANCE = instance
            instance
        }
    }

    // ✅ Cargar materias solo una vez
    fun loadInitialMaterias(context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val isMateriasLoaded = sharedPreferences.getBoolean("materias_loaded", false)

        if (!isMateriasLoaded) {
            Log.d("DatabaseProvider", "Iniciando la carga de materias")

            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)
                val materias = listOf(
                    Materia(1, "TECNICAS"),
                    Materia(2, "PROCEDIMIENTOS"),
                    Materia(3, "ADMINISTRACION"),
                    Materia(4, "URGENCIAS")
                )

                database.materiaDao().insertAll(materias)
                Log.d("DatabaseProvider", "Materias insertadas en Room: $materias")

                // Marcar las materias como cargadas
                sharedPreferences.edit().putBoolean("materias_loaded", true).apply()
                Log.d("DatabaseProvider", "Materias marcadas como cargadas en SharedPreferences")
            }
        } else {
            Log.d("DatabaseProvider", "Las materias ya fueron cargadas previamente")
        }
    }

}

