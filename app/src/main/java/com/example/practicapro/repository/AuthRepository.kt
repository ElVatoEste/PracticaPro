import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.practicapro.model.LoginRequest
import com.example.practicapro.rooms.entitys.User
import com.example.practicapro.network.ApiClient
import com.example.practicapro.network.AuthService


object AuthRepository {
    var useMockLogin: Boolean = true

    private val authService = ApiClient.retrofit.create(AuthService::class.java)

    suspend fun login(context: Context, email: String, password: String): Result<User> {
        return try {
            if (isNetworkAvailable(context) && !useMockLogin) {

                val response = authService.login(LoginRequest(email, password))
                val expirationDate = System.currentTimeMillis() + (response.expiresIn * 1000)

                val user = User(
                    username = response.user.nombre,
                    email = response.user.email,
                    token = response.accessToken,
                    expirationDate = expirationDate
                )
                Result.success(user)
            } else {
                mockLogin(email, password)?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Credenciales inválidas en modo mock."))
            }
        } catch (e: Exception) {
            Result.failure(e)
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

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
