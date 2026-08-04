package com.vatodev.practicapro.rooms.appDatabase

import android.content.Context
import androidx.room.Room
import com.vatodev.practicapro.model.MATERIAS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    /**
     * Siembra el catálogo de materias. `insertAll` usa REPLACE, así que
     * ejecutarlo de más es inofensivo y no hace falta una bandera aparte.
     */
    suspend fun loadInitialMaterias(context: Context) {
        withContext(Dispatchers.IO) {
            getDatabase(context).materiaDao().insertAll(MATERIAS)
        }
    }
}
