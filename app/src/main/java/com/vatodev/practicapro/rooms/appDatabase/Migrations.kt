package com.vatodev.practicapro.rooms.appDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Hubo builds internas con las columnas de la 15 sobre una base todavía
 * marcada como 14. Comprobar antes de añadir evita el `duplicate column name`
 * que dejaría la actualización a medias.
 */
private fun SupportSQLiteDatabase.tieneColumna(tabla: String, columna: String): Boolean =
    query("PRAGMA table_info($tabla)").use { cursor ->
        val nombre = cursor.getColumnIndexOrThrow("name")
        while (cursor.moveToNext()) {
            if (cursor.getString(nombre) == columna) return true
        }
        false
    }

private fun SupportSQLiteDatabase.anadirColumna(tabla: String, columna: String, tipo: String) {
    if (!tieneColumna(tabla, columna)) execSQL("ALTER TABLE $tabla ADD COLUMN $columna $tipo")
}

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

/** Añade el progreso por técnica: en qué paso se quedó el usuario. */
val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS progreso_tecnica (
                 clave TEXT NOT NULL,
                 modulo TEXT NOT NULL,
                 titulo TEXT NOT NULL,
                 pasoActual INTEGER NOT NULL,
                 totalPasos INTEGER NOT NULL,
                 actualizado INTEGER NOT NULL,
                 PRIMARY KEY(clave)
               )"""
        )
    }
}

/**
 * Cuentas locales múltiples.
 *
 * `user` deja de ser fila única y gana contraseña; `note` y `progreso_tecnica`
 * ganan dueño. Lo existente se atribuye a la cuenta que ya hubiera, que entra
 * sin contraseña hasta que fije una.
 */
val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.anadirColumna("user", "passwordHash", "TEXT NOT NULL DEFAULT ''")
        db.anadirColumna("user", "salt", "TEXT NOT NULL DEFAULT ''")
        db.anadirColumna("user", "creada", "INTEGER NOT NULL DEFAULT 0")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_user_email ON user (email)")

        if (!db.tieneColumna("note", "userId")) {
            db.execSQL("ALTER TABLE note ADD COLUMN userId INTEGER NOT NULL DEFAULT -1")
            db.execSQL(
                "UPDATE note SET userId = COALESCE((SELECT id FROM user ORDER BY id LIMIT 1), -1)"
            )
        }
        db.execSQL("CREATE INDEX IF NOT EXISTS index_note_userId ON note (userId)")

        if (db.tieneColumna("progreso_tecnica", "userId")) return

        db.execSQL(
            """CREATE TABLE progreso_nuevo (
                 clave TEXT NOT NULL,
                 userId INTEGER NOT NULL,
                 modulo TEXT NOT NULL,
                 titulo TEXT NOT NULL,
                 pasoActual INTEGER NOT NULL,
                 totalPasos INTEGER NOT NULL,
                 actualizado INTEGER NOT NULL,
                 PRIMARY KEY(clave, userId)
               )"""
        )
        db.execSQL(
            """INSERT INTO progreso_nuevo (clave, userId, modulo, titulo, pasoActual, totalPasos, actualizado)
               SELECT clave, COALESCE((SELECT id FROM user ORDER BY id LIMIT 1), -1),
                      modulo, titulo, pasoActual, totalPasos, actualizado
               FROM progreso_tecnica"""
        )
        db.execSQL("DROP TABLE progreso_tecnica")
        db.execSQL("ALTER TABLE progreso_nuevo RENAME TO progreso_tecnica")
    }
}

val ALL_MIGRATIONS = arrayOf(
    MIGRATION_11_12,
    MIGRATION_12_13,
    MIGRATION_13_14,
    MIGRATION_14_15
)
