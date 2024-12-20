import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.practicapro.network.NetworkObserver
import kotlinx.coroutines.launch

@Composable
fun ConnectivityIndicator(snackbarHostState: SnackbarHostState) {
    val isNetworkAvailable = NetworkObserver.isNetworkAvailable.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(isNetworkAvailable.value) {
        val message = if (isNetworkAvailable.value) {
            "Conexión restaurada"
        } else {
            "Sin conexión a Internet"
        }

        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite,
                actionLabel = "Cerrar"
            )
        }
    }
}