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
        composable("tecnicas") { AsepsiaScreen() }
        composable("procedimientos") { ProcedimientosScreen() }
        composable("administracion") { MedicamentosScreen() }
        composable("urgencias") { UrgenciasScreen() }
    }
}
