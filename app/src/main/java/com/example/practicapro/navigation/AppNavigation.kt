package com.example.practicapro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.practicapro.ui.splash.SplashScreen
import com.example.practicapro.ui.main.MainScreen
import com.example.practicapro.ui.calculadora.CalculadoraScreen
import com.example.practicapro.ui.login.LoginScreen
import com.example.practicapro.ui.register.RegisterScreen
import com.example.practicapro.ui.study.asepsia.AsepsiaScreen
import com.example.practicapro.ui.study.asepsia.QuizScreen
import com.example.practicapro.ui.study.procedimientos.ProcedimientosScreen
import com.example.practicapro.ui.study.medicamentos.MedicamentosScreen
import com.example.practicapro.ui.study.medicamentos.MinijuegoMedicamentosScreen
import com.example.practicapro.ui.study.urgencias.UrgenciasScreen
import com.example.practicapro.ui.study.procedimientos.ProcedimientosQuizScreen

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
    const val MINIJUEGO_MEDICAMENTOS = "minijuego_medicamentos"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                context = LocalContext.current,
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                context = LocalContext.current
            )
        }

        composable(Routes.MAIN) {
            MainScreen(navController)
        }

        composable(Routes.CALCULADORA) {
            CalculadoraScreen()
        }

        composable(Routes.TECNICAS) {
            AsepsiaScreen(navController)
        }

        composable(Routes.QUIZ_SCREEN) {
            QuizScreen(onDismiss = { navController.popBackStack() })
        }

        composable(Routes.PROCEDIMIENTOS) {
            ProcedimientosScreen(navController)
        }

        composable(Routes.QUIZ_PROCEDIMIENTOS) {
            ProcedimientosQuizScreen(onDismiss = { navController.popBackStack() })
        }

        composable(Routes.ADMINISTRACION) {
            MedicamentosScreen(navController)
        }

        composable(Routes.MINIJUEGO_MEDICAMENTOS) {
            MinijuegoMedicamentosScreen(navController)
        }

        composable(Routes.URGENCIAS) {
            UrgenciasScreen(navController)
        }
    }
}
