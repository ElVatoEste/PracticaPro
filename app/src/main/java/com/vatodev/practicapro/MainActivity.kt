package com.vatodev.practicapro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.vatodev.practicapro.network.ConnectivityIndicator
import com.vatodev.practicapro.navigation.AppNavigation
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.ui.navbar.BottomNavigationBar
import com.vatodev.practicapro.network.NetworkObserver
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.vatodev.practicapro.network.AppLifecycleObserver
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import com.vatodev.practicapro.ui.theme.PracticaproTheme
import com.vatodev.practicapro.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // 🔧 Inicializa el UserViewModel
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))

        NetworkObserver.startObserving(this)

        lifecycleScope.launch {
            DatabaseProvider.loadInitialMaterias(applicationContext)
            userViewModel.loadTokenFromRoom(applicationContext)
        }

        setContent {
            val temaOscuro = isSystemInDarkTheme()

            // Los iconos de las barras del sistema se dibujan sobre el fondo de
            // la app, así que su claro/oscuro debe ser el contrario al tema.
            LaunchedEffect(temaOscuro) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !temaOscuro
                    isAppearanceLightNavigationBars = !temaOscuro

                    // Modo inmersivo: la app ocupa toda la altura. Las barras
                    // reaparecen al deslizar desde el borde y se vuelven a
                    // ocultar solas.
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    hide(WindowInsetsCompat.Type.systemBars())
                }
            }

            PracticaproTheme(darkTheme = temaOscuro) {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.displayCutout),
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentRoute !in Routes.SIN_BARRA) {
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
}

@Composable
fun AppContent(modifier: Modifier = Modifier, navController: NavHostController) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        AppNavigation(navController)
    }
}

