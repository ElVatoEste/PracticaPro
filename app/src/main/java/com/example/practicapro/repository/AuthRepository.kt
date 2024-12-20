import android.content.Context
import com.example.practicapro.model.LoginRequest
import com.example.practicapro.rooms.entitys.User
import com.example.practicapro.network.ApiClient
import com.example.practicapro.network.AuthService
import com.example.practicapro.network.NetworkObserver
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthRepository {
    var useMockLogin: Boolean = false

    private val authService = ApiClient.retrofit.create(AuthService::class.java)

    suspend fun login(context: Context, email: String, password: String): Result<User> {
        return try {
            // Verificar conectividad usando NetworkObserver
            val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()

            val user = if (isNetworkAvailable && !useMockLogin) {
                // Llamada a la API
                val response = authService.login(LoginRequest(email, password))
                val expirationDate = System.currentTimeMillis() + (response.expiresIn * 1000)

                User(
                    username = response.user.nombre,
                    email = response.user.email,
                    token = response.accessToken,
                    expirationDate = expirationDate
                )
            } else {
                // Modo mock si no hay conectividad o se fuerza el mock
                mockLogin(email, password) ?: return Result.failure(Exception("Credenciales inválidas en modo mock."))
            }

            // Guardar el usuario en Room
            saveUserToDatabase(context, user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveUserToDatabase(context: Context, user: User) {
        val userDao = DatabaseProvider.getDatabase(context).userDao()
        withContext(Dispatchers.IO) {
            userDao.insertUser(user)
        }
    }

    private fun mockLogin(email: String, password: String): User? {
        return if (email == "test@test.com" && password == "password") {
            User(
                username = "Test User",
                email = "test@test.com",
                token = "mock_token_123",
                expirationDate = System.currentTimeMillis() + 3600000 // 1 hora
            )
        } else {
            null
        }
    }
}
