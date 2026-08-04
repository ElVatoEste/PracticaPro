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

val ALL_MIGRATIONS = arrayOf(MIGRATION_11_12)
