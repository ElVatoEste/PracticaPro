package com.example.practicapro.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.practicapro.rooms.entitys.PendingRequest

@Dao
interface PendingRequestDao {

    @Insert
    suspend fun insertRequest(request: PendingRequest)

    @Query("SELECT * FROM pending_requests")
    suspend fun getAllRequests(): List<PendingRequest>

    @Query("DELETE FROM pending_requests WHERE id = :requestId")
    suspend fun deleteRequestById(requestId: Int)
}
