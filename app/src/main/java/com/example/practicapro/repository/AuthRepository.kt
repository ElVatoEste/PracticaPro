package com.example.practicapro.repository

import com.example.practicapro.rooms.entitys.User

object AuthRepository {
    fun mockLogin(username: String, password: String): User? {
        return if (username == "test" && password == "password") {
            User(
                username = "Test User",
                email = "testuser@example.com",
                token = "mock_token_123",
                expirationDate = System.currentTimeMillis() + 3600000 // Token válido por 1 hora
            )
        } else {
            null
        }
    }
}
