package com.example.practicapro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.example.practicapro.network.ConnectivityIndicator
import com.example.practicapro.navigation.AppNavigation
import com.example.practicapro.navigation.Routes
import com.example.practicapro.ui.navbar.BottomNavigationBar
import com.example.practicapro.network.NetworkObserver
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.practicapro.network.ApiClient
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import com.example.practicapro.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // 🔧 Inicializa el UserViewModel
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkObserver.startObserving(this)

        // Cargar materias solo una vez
        DatabaseProvider.loadInitialMaterias(this)

        // 🔄 Cargar el token desde Room y actualizar el ApiClient
        lifecycleScope.launch {
            userViewModel.loadTokenFromRoom(applicationContext)

            // 🔧 Log para saber si se obtuvo el token
            val token = userViewModel.token.value
            if (token != null) {
                Log.d("Observe", "Token obtenido: $token")
                ApiClient.setToken(token)
                Log.d("Observe", "Token configurado en ApiClient")
            } else {
                Log.d("Observe", "No se encontró token en Room")
            }
        }

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                    if (currentRoute != Routes.LOGIN && currentRoute != Routes.SPLASH && currentRoute != Routes.REGISTER) {
                        BottomNavigationBar(navController, userViewModel)
                    }
                }
            ) { innerPadding ->
                ConnectivityIndicator(snackbarHostState = snackbarHostState)
                AppContent(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun AppContent(modifier: Modifier = Modifier, navController: NavHostController) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        AppNavigation(navController)
    }
}
