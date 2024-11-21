package com.example.practicapro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.practicapro.ui.screen.splash.SplashScreen
import com.example.practicapro.ui.screen.main.MainScreen
import com.example.practicapro.ui.screen.calculadora.CalculadoraScreen
import com.example.practicapro.ui.screen.modules.AsepsiaScreen
import com.example.practicapro.ui.screen.modules.ProcedimientosScreen
import com.example.practicapro.ui.screen.modules.MedicamentosScreen
import com.example.practicapro.ui.screen.modules.UrgenciasScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        // Pantalla Splash
        composable("splash") { SplashScreen(navController) }
        // Pantalla Principal
        composable("main") { MainScreen(navController) }
        // Pantallas de los Módulos
        composable("calculadora") { CalculadoraScreen() }
        composable("tecnicas_de_asepsia_y_antisepsia") { AsepsiaScreen() }
        composable("procedimientos_basicos") { ProcedimientosScreen() }
        composable("administracion_de_medicamentos") { MedicamentosScreen() }
        composable("urgencias_medicas") { UrgenciasScreen() }
    }
}
