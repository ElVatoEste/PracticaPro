package com.vatodev.practicapro.rooms.appDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Añade el estado de sincronización a `note`.
 *
 * Las filas con id positivo vinieron del servidor, así que se marcan como
 * sincronizadas. Las negativas las creó [com.vatodev.practicapro.repository.NotesRepository]
 * en local y quedan pendientes.
 */
val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE note ADD COLUMN remoteId INTEGER")
        db.execSQL("ALTER TABLE note ADD COLUMN synced INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE note ADD COLUMN dateMillis INTEGER NOT NULL DEFAULT 0")

        db.execSQL("UPDATE note SET remoteId = id, synced = 1 WHERE id > 0")
        db.execSQL("UPDATE note SET dateMillis = CAST(date AS INTEGER) WHERE date GLOB '[0-9]*'")
    }
}

/**
 * Retira la columna heredada `note.date` y la tabla `pending_requests`.
 *
 * `date` guardaba la fecha como texto sin formato garantizado; `dateMillis` la
 * sustituye desde la versión 12. La cola de peticiones quedó obsoleta al
 * llevar cada nota su propio `synced`: reconciliar es ahora
 * `SELECT * FROM note WHERE synced = 0`, sin una estructura paralela que
 * mantener en sincronía ni crecer sin techo.
 *
 * SQLite en minSdk 30 no admite DROP COLUMN, así que la tabla se recrea.
 */
val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE note_nueva (
                 id INTEGER NOT NULL,
                 remoteId INTEGER,
                 synced INTEGER NOT NULL DEFAULT 0,
                 score INTEGER NOT NULL,
                 attempt INTEGER NOT NULL,
                 dateMillis INTEGER NOT NULL DEFAULT 0,
                 subjectId INTEGER NOT NULL,
                 subjectName TEXT NOT NULL,
                 PRIMARY KEY(id)
               )"""
        )
        db.execSQL(
            """INSERT INTO note_nueva (id, remoteId, synced, score, attempt, dateMillis, subjectId, subjectName)
               SELECT id, remoteId, synced, score, attempt, dateMillis, subjectId, subjectName FROM note"""
        )
        db.execSQL("DROP TABLE note")
        db.execSQL("ALTER TABLE note_nueva RENAME TO note")

        db.execSQL("DROP TABLE IF EXISTS pending_requests")
    }
}

val ALL_MIGRATIONS = arrayOf(MIGRATION_11_12, MIGRATION_12_13)
