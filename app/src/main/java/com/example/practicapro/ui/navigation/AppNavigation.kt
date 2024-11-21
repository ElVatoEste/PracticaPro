package com.example.practicapro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.practicapro.ui.screen.splash.SplashScreen
import com.example.practicapro.ui.screen.main.MainScreen
import com.example.practicapro.ui.screen.calculadora.CalculadoraScreen
import com.example.practicapro.ui.asepsia.AsepsiaScreen
import com.example.practicapro.ui.asepsia.QuizScreen
import com.example.practicapro.ui.procedimientos.ProcedimientosScreen
import com.example.practicapro.ui.screen.modules.MedicamentosScreen
import com.example.practicapro.ui.screen.modules.UrgenciasScreen

// Definición de rutas como constantes
object Routes {
    const val SPLASH = "splash"                  // Ruta para la pantalla de bienvenida
    const val MAIN = "main"                      // Ruta para la pantalla principal
    const val CALCULADORA = "calculadora"        // Ruta para la calculadora
    const val TECNICAS = "tecnicas"              // Ruta para el módulo de técnicas de asepsia
    const val QUIZ_SCREEN = "quiz_screen"        // Ruta para el quiz de técnicas de asepsia
    const val PROCEDIMIENTOS = "procedimientos"  // Ruta para el módulo de procedimientos básicos
    const val ADMINISTRACION = "administracion"  // Ruta para el módulo de administración de medicamentos
    const val URGENCIAS = "urgencias"            // Ruta para el módulo de urgencias médicas
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        // Pantalla Splash
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        // Pantalla Principal
        composable(Routes.MAIN) {
            MainScreen(navController)
        }

        // Pantalla de la Calculadora
        composable(Routes.CALCULADORA) {
            CalculadoraScreen()
        }

        // Pantalla de Técnicas de Asepsia
        composable(Routes.TECNICAS) {
            AsepsiaScreen(navController)
        }

        // Pantalla del Quiz de Técnicas
        composable(Routes.QUIZ_SCREEN) {
            QuizScreen(onDismiss = { navController.popBackStack() })
        }

        // Pantalla de Procedimientos Básicos
        composable(Routes.PROCEDIMIENTOS) {
            ProcedimientosScreen(navController)
        }

        // Pantalla de Administración de Medicamentos
        composable(Routes.ADMINISTRACION) {
            MedicamentosScreen()
        }

        // Pantalla de Urgencias Médicas
        composable(Routes.URGENCIAS) {
            UrgenciasScreen()
        }
    }
}
