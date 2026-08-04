package com.vatodev.practicapro.rooms

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vatodev.practicapro.rooms.appDatabase.AppDatabase
import com.vatodev.practicapro.rooms.appDatabase.MIGRATION_11_12
import com.vatodev.practicapro.rooms.appDatabase.MIGRATION_12_13
import com.vatodev.practicapro.rooms.appDatabase.MIGRATION_13_14
import com.vatodev.practicapro.rooms.appDatabase.MIGRATION_14_15
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * La versión 11 se publicó con `fallbackToDestructiveMigration`, así que hay
 * instalaciones reales en ese esquema. Si la 11 → 12 falla, esas notas se
 * pierden sin aviso.
 */
@RunWith(AndroidJUnit4::class)
class MigracionTest {

    private val BD = "migracion-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migracion11a12ConservaLasNotas() {
        helper.createDatabase(BD, 11).use { db ->
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (7, 85, 1, '1735689600000', 1, 'TECNICAS')"""
            )
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (-1, 60, 2, '1735689600000', 2, 'PROCEDIMIENTOS')"""
            )
        }

        val db = helper.runMigrationsAndValidate(BD, 12, true, MIGRATION_11_12)

        db.query("SELECT COUNT(*) FROM note").use {
            it.moveToFirst()
            assertEquals(2, it.getInt(0))
        }
    }

    /** Id positivo vino del servidor; negativo lo creó la app en local. */
    @Test
    fun migracion11a12MarcaComoSincronizadasSoloLasNotasDelServidor() {
        helper.createDatabase(BD, 11).use { db ->
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (7, 85, 1, '1735689600000', 1, 'TECNICAS')"""
            )
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (-1, 60, 2, 'sin fecha', 2, 'PROCEDIMIENTOS')"""
            )
        }

        val db = helper.runMigrationsAndValidate(BD, 12, true, MIGRATION_11_12)

        db.query("SELECT synced, remoteId FROM note WHERE id = 7").use {
            it.moveToFirst()
            assertEquals(1, it.getInt(0))
            assertEquals(7, it.getInt(1))
        }
        db.query("SELECT synced, remoteId FROM note WHERE id = -1").use {
            it.moveToFirst()
            assertEquals(0, it.getInt(0))
            assertTrue(it.isNull(1))
        }
    }

    @Test
    fun migracion11a12ParseaLaFechaNumericaYDejaCeroSiNoLoEs() {
        helper.createDatabase(BD, 11).use { db ->
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (7, 85, 1, '1735689600000', 1, 'TECNICAS')"""
            )
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (8, 70, 1, '2025-01-01', 2, 'PROCEDIMIENTOS')"""
            )
        }

        val db = helper.runMigrationsAndValidate(BD, 12, true, MIGRATION_11_12)

        db.query("SELECT dateMillis FROM note WHERE id = 7").use {
            it.moveToFirst()
            assertEquals(1735689600000L, it.getLong(0))
        }
        // "2025-01-01" empieza por dígito: CAST se queda en 2025, no en 0.
        db.query("SELECT dateMillis FROM note WHERE id = 8").use {
            it.moveToFirst()
            assertEquals(2025L, it.getLong(0))
        }
    }

    @Test
    fun migracion11a12ConservaElUsuario() {
        helper.createDatabase(BD, 11).use { db ->
            db.execSQL(
                """INSERT INTO user (id, username, email, token, expirationDate)
                   VALUES (-1, 'Ana', 'ana@example.com', '', 9223372036854775807)"""
            )
        }

        val db = helper.runMigrationsAndValidate(BD, 12, true, MIGRATION_11_12)

        db.query("SELECT username FROM user WHERE id = -1").use {
            it.moveToFirst()
            assertEquals("Ana", it.getString(0))
        }
    }

    /** La 12 → 13 recrea `note` porque SQLite en minSdk 30 no admite DROP COLUMN. */
    @Test
    fun migracion12a13ConservaLasNotasAlRecrearLaTabla() {
        helper.createDatabase(BD, 12).use { db ->
            db.execSQL(
                """INSERT INTO note (id, remoteId, synced, score, attempt, date, dateMillis, subjectId, subjectName)
                   VALUES (7, 7, 1, 85, 1, '1735689600000', 1735689600000, 1, 'TECNICAS')"""
            )
            db.execSQL(
                """INSERT INTO note (id, remoteId, synced, score, attempt, date, dateMillis, subjectId, subjectName)
                   VALUES (-1, NULL, 0, 60, 2, 'sin fecha', 0, 2, 'PROCEDIMIENTOS')"""
            )
        }

        val db = helper.runMigrationsAndValidate(BD, 13, true, MIGRATION_12_13)

        db.query("SELECT id, remoteId, synced, score, attempt, dateMillis, subjectName FROM note ORDER BY id").use {
            assertEquals(2, it.count)

            it.moveToFirst()
            assertEquals(-1, it.getInt(0))
            assertTrue(it.isNull(1))
            assertEquals(0, it.getInt(2))
            assertEquals(60, it.getInt(3))

            it.moveToNext()
            assertEquals(7, it.getInt(0))
            assertEquals(7, it.getInt(1))
            assertEquals(1, it.getInt(2))
            assertEquals(1735689600000L, it.getLong(5))
        }
    }

    @Test
    fun migracion12a13RetiraLaColaDePeticiones() {
        helper.createDatabase(BD, 12).use { db ->
            db.execSQL(
                """INSERT INTO pending_requests (endpoint, payload, method, userId, timestamp)
                   VALUES ('notas', '{}', 'POST', -1, 0)"""
            )
        }

        val db = helper.runMigrationsAndValidate(BD, 13, true, MIGRATION_12_13)

        db.query(
            "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'pending_requests'"
        ).use {
            assertEquals(0, it.count)
        }
    }

    /** Una instalación en la 11 debe llegar a la 13 sin perder nada. */
    @Test
    fun cadenaCompleta11a13ConservaLasNotas() {
        helper.createDatabase(BD, 11).use { db ->
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (7, 85, 1, '1735689600000', 1, 'TECNICAS')"""
            )
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (-1, 60, 2, 'sin fecha', 2, 'PROCEDIMIENTOS')"""
            )
        }

        val db = helper.runMigrationsAndValidate(BD, 13, true, MIGRATION_11_12, MIGRATION_12_13)

        db.query("SELECT id, synced, dateMillis FROM note ORDER BY id").use {
            assertEquals(2, it.count)

            it.moveToFirst()
            assertEquals(-1, it.getInt(0))
            assertEquals(0, it.getInt(1))

            it.moveToNext()
            assertEquals(7, it.getInt(0))
            assertEquals(1, it.getInt(1))
            assertEquals(1735689600000L, it.getLong(2))
        }
    }

    @Test
    fun migracion13a14CreaElProgresoSinTocarLoExistente() {
        helper.createDatabase(BD, 13).use { db ->
            db.execSQL(
                """INSERT INTO note (id, remoteId, synced, score, attempt, dateMillis, subjectId, subjectName)
                   VALUES (7, 7, 1, 85, 1, 1735689600000, 1, 'TECNICAS')"""
            )
        }

        val db = helper.runMigrationsAndValidate(BD, 14, true, MIGRATION_13_14)

        db.query("SELECT COUNT(*) FROM note").use {
            it.moveToFirst()
            assertEquals(1, it.getInt(0))
        }
        db.query("SELECT COUNT(*) FROM progreso_tecnica").use {
            it.moveToFirst()
            assertEquals(0, it.getInt(0))
        }
    }

    /** Lo existente pasa a pertenecer a la cuenta que ya hubiera. */
    @Test
    fun migracion14a15AtribuyeNotasYProgresoALaCuentaExistente() {
        helper.createDatabase(BD, 14).use { db ->
            db.execSQL(
                """INSERT INTO user (id, username, email, token, expirationDate)
                   VALUES (-7, 'Ana', 'ana@example.com', '', 9223372036854775807)"""
            )
            db.execSQL(
                """INSERT INTO note (id, remoteId, synced, score, attempt, dateMillis, subjectId, subjectName)
                   VALUES (-1, NULL, 0, 85, 1, 1735689600000, 1, 'TECNICAS')"""
            )
            db.execSQL(
                """INSERT INTO progreso_tecnica (clave, modulo, titulo, pasoActual, totalPasos, actualizado)
                   VALUES ('stepsLavadoClinico', 'asepsia', 'Lavado clínico', 3, 10, 1735689600000)"""
            )
        }

        val db = helper.runMigrationsAndValidate(BD, 15, true, MIGRATION_14_15)

        db.query("SELECT userId FROM note WHERE id = -1").use {
            it.moveToFirst()
            assertEquals(-7, it.getInt(0))
        }
        db.query("SELECT userId, pasoActual FROM progreso_tecnica WHERE clave = 'stepsLavadoClinico'").use {
            it.moveToFirst()
            assertEquals(-7, it.getInt(0))
            assertEquals(3, it.getInt(1))
        }
        db.query("SELECT passwordHash, salt FROM user WHERE id = -7").use {
            it.moveToFirst()
            assertEquals("", it.getString(0))
            assertEquals("", it.getString(1))
        }
    }

    /**
     * El recorrido que hará una instalación publicada al actualizar: de la
     * versión 11 a la actual, sin perder nada.
     */
    @Test
    fun cadenaCompleta11a15ConservaNotasUsuarioYProgreso() {
        helper.createDatabase(BD, 11).use { db ->
            db.execSQL(
                """INSERT INTO user (id, username, email, token, expirationDate)
                   VALUES (-1, 'Ana', 'ana@example.com', '', 9223372036854775807)"""
            )
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (7, 85, 1, '1735689600000', 1, 'TECNICAS')"""
            )
            db.execSQL(
                """INSERT INTO note (id, score, attempt, date, subjectId, subjectName)
                   VALUES (-1, 60, 2, 'sin fecha', 2, 'PROCEDIMIENTOS')"""
            )
        }

        val db = helper.runMigrationsAndValidate(
            BD, 15, true,
            MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15
        )

        db.query("SELECT username FROM user WHERE id = -1").use {
            it.moveToFirst()
            assertEquals("Ana", it.getString(0))
        }
        db.query("SELECT id, synced, userId, dateMillis FROM note ORDER BY id").use {
            assertEquals(2, it.count)

            it.moveToFirst()
            assertEquals(-1, it.getInt(0))
            assertEquals(0, it.getInt(1))
            assertEquals(-1, it.getInt(2))

            it.moveToNext()
            assertEquals(7, it.getInt(0))
            assertEquals(1, it.getInt(1))
            assertEquals(-1, it.getInt(2))
            assertEquals(1735689600000L, it.getLong(3))
        }
    }
}
