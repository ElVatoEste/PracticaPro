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
                // Las versiones 1 a 10 son anteriores a las migraciones reales
                // y no hay camino desde ellas. Recrear es preferible a fallar
                // al abrir: esas instalaciones ya perdían la base en cada
                // actualización, así que no hay nada que conservar.
                .fallbackToDestructiveMigrationFrom(true, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .build()
            INSTANCE = instance
            instance
        }
    }

    /**
     * Vuelca el WAL al archivo principal y lo vacía.
     *
     * La copia automática de Android solo respalda `app_database`: sin volcar,
     * lo escrito desde el último punto de control vive en `app_database-wal` y
     * no viaja en la copia. Se llama al pasar a segundo plano, que es cuando
     * el sistema puede decidir respaldar.
     *
     * No abre la base si nadie la abrió antes: sin escrituras no hay nada que
     * volcar.
     */
    suspend fun volcarWal() {
        val db = INSTANCE ?: return
        withContext(Dispatchers.IO) {
            runCatching {
                db.openHelper.writableDatabase
                    .query("PRAGMA wal_checkpoint(TRUNCATE)")
                    .use { it.moveToFirst() }
            }
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
