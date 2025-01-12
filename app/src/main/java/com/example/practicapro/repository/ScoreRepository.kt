package com.example.practicapro.repository

import android.content.Context
import com.example.practicapro.network.NetworkObserver
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import com.example.practicapro.rooms.entitys.Score
import kotlinx.coroutines.flow.first

class ScoreRepository(private val context: Context) {

    private val scoreDao = DatabaseProvider.getDatabase(context).scoreDao()

    // Obtiene el número de intentos del usuario
    suspend fun getAttemptCount(userId: Int, quizName: String): Int {
        return scoreDao.getAttemptCount(userId, quizName)
    }

    // Guarda el puntaje de forma offline-first
    suspend fun saveScore(score: Score) {
        scoreDao.insertScore(score)
        val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
        if (isNetworkAvailable) {
            // Sincroniza con el servidor cuando haya conexión
            syncScores()
        }
    }

    // Sincroniza los puntajes locales con el servidor
    private suspend fun syncScores() {
        val unsyncedScores = scoreDao.getScores(userId = 1, quizName = "Asepsia Quiz") // Solo ejemplo
        if (unsyncedScores.isNotEmpty()) {
            // TODO: Implementar lógica para sincronizar los puntajes con el servidor
        }
    }
}
