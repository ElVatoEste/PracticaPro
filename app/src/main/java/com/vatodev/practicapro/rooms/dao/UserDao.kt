package com.vatodev.practicapro.rooms.dao

import androidx.room.*
import com.vatodev.practicapro.rooms.entitys.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): User?

    @Query("DELETE FROM user")
    suspend fun deleteUser()
}
