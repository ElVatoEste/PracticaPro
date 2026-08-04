package com.vatodev.practicapro.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vatodev.practicapro.rooms.entitys.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    /** Todas las cuentas locales del dispositivo, la más reciente primero. */
    @Query("SELECT * FROM user ORDER BY creada DESC")
    suspend fun todas(): List<User>

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun porId(id: Int): User?

    @Query("SELECT * FROM user WHERE email = :email COLLATE NOCASE LIMIT 1")
    suspend fun porEmail(email: String): User?

    @Query("SELECT COUNT(*) FROM user")
    suspend fun cuantas(): Int

    /** Id local libre: el menor menos uno, ya que las cuentas locales son negativas. */
    @Query("SELECT MIN(id) FROM user")
    suspend fun idMinimo(): Int?

    @Query("DELETE FROM user WHERE id = :id")
    suspend fun borrar(id: Int)

    @Query("DELETE FROM user")
    suspend fun deleteUser()
}
