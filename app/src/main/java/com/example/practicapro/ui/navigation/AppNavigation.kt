package com.example.practicapro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.practicapro.ui.screen.splash.SplashScreen
import com.example.practicapro.ui.screen.main.MainScreen
import com.example.practicapro.ui.screen.calculadora.CalculadoraScreen
import com.example.practicapro.ui.screen.asepsia.AsepsiaScreen
import com.example.practicapro.ui.screen.asepsia.QuizScreen
import com.example.practicapro.ui.screen.procedimientos.ProcedimientosScreen
import com.example.practicapro.ui.screen.medicamentos.MedicamentosScreen
import com.example.practicapro.ui.screen.modules.UrgenciasScreen
import com.example.practicapro.ui.screen.procedimientos.ProcedimientosQuizScreen

// Definición de rutas como constantes
object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val CALCULADORA = "calculadora"
    const val TECNICAS = "tecnicas"
    const val QUIZ_SCREEN = "quiz_screen"
    const val PROCEDIMIENTOS = "procedimientos"
    const val QUIZ_PROCEDIMIENTOS = "quiz_procedimientos"
    const val ADMINISTRACION = "administracion"
    const val URGENCIAS = "urgencias"
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

        // Pantalla del Quiz de Procedimientos Básicos
        composable(Routes.QUIZ_PROCEDIMIENTOS) {
            ProcedimientosQuizScreen(onDismiss = { navController.popBackStack() })
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
