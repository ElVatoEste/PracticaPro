package com.example.practicapro.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.practicapro.rooms.entitys.Score

@Dao
interface ScoreDao {

//    @Query("SELECT * FROM score WHERE userId = :userId AND quizName = :quizName")
//    suspend fun getScores(userId: Int, quizName: String): List<Score>
//
//    @Query("SELECT COUNT(*) FROM score WHERE userId = :userId AND quizName = :quizName")
//    suspend fun getAttemptCount(userId: Int, quizName: String): Int
//
//    @Insert
//    suspend fun insertScore(score: Score)
}
