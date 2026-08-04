package com.vatodev.practicapro.rooms.appDatabase

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.vatodev.practicapro.rooms.entitys.Materia
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
                .addMigrations(*ALL_MIGRATIONS)
                .build()
            INSTANCE = instance
            instance
        }
    }

    // ✅ Cargar materias solo una vez
    fun loadInitialMaterias(context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val isMateriasLoaded = sharedPreferences.getBoolean("materias_loaded", false)

        CoroutineScope(Dispatchers.IO).launch {
            val database = getDatabase(context)
            val materiasEnRoom = database.materiaDao().getAllMaterias()

            if (!isMateriasLoaded || materiasEnRoom.isEmpty()) {
                Log.d("DatabaseProvider", "Iniciando la carga de materias")

                val materias = listOf(
                    Materia(1, "TECNICAS"),
                    Materia(2, "PROCEDIMIENTOS"),
                    Materia(3, "ADMINISTRACION"),
                    Materia(4, "URGENCIAS"),
                    Materia(5, "PROCEDIMIENTOS2"),
                )

                // Insertar materias en Room
                database.materiaDao().insertAll(materias)
                Log.d("DatabaseProvider", "Materias insertadas en Room: $materias")

                // Verificar si las materias están realmente en Room
                val materiasVerificadas = database.materiaDao().getAllMaterias()
                Log.d("DatabaseProvider", "Materias actualmente en Room: $materiasVerificadas")

                // Actualizar SharedPreferences solo si las materias están en Room
                if (materiasVerificadas.isNotEmpty()) {
                    sharedPreferences.edit().putBoolean("materias_loaded", true).apply()
                    Log.d("DatabaseProvider", "Materias marcadas como cargadas en SharedPreferences")
                } else {
                    Log.e("DatabaseProvider", "Error: No se encontraron materias en Room después de la inserción")
                }
            } else {
                Log.d("DatabaseProvider", "Las materias ya fueron cargadas previamente")
            }
        }
    }
}

