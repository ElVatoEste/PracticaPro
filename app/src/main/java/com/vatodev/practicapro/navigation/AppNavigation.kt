package com.vatodev.practicapro.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vatodev.practicapro.ui.splash.SplashScreen
import com.vatodev.practicapro.ui.main.MainScreen
import com.vatodev.practicapro.ui.calculadora.CalculadoraScreen
import com.vatodev.practicapro.ui.calculadora.ImcScreen
import com.vatodev.practicapro.ui.calculadora.PamScreen
import com.vatodev.practicapro.ui.login.LoginScreen
import com.vatodev.practicapro.ui.register.RegisterScreen
import com.vatodev.practicapro.ui.study.asepsia.AsepsiaScreen
import com.vatodev.practicapro.ui.study.asepsia.quiz.QuizScreen
import com.vatodev.practicapro.ui.study.procedimientos.ProcedScreen
import com.vatodev.practicapro.ui.study.medicamentos.MedicamentosScreen
import com.vatodev.practicapro.ui.study.procedimientos.quiz.ProcQuiz
import com.vatodev.practicapro.ui.study.procedimientos.quiz.TrueFalseQuizScreen
import com.vatodev.practicapro.ui.study.urgencias.UrgenciasScreen

object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val CALCULADORA = "calculadora"
    const val TECNICAS = "tecnicas"
    const val QUIZ_SCREEN = "quiz_screen"
    const val PROCEDIMIENTOS = "procedimientos"
    const val QUIZ_PROCEDIMIENTOS = "quiz_procedimientos"
    const val QUIZ_PROC_TF = "quiz_proc_tf"
    const val ADMINISTRACION = "administracion"
    const val URGENCIAS = "urgencias"
    const val MINIJUEGO_MEDICAMENTOS = "minijuego_medicamentos"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val IMC = "imc"
    const val PAM = "pam"
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

        // Login Screen
        composable(
            Routes.LOGIN,
            enterTransition = { slideInVertically() + fadeIn() },
            exitTransition = { slideOutVertically() + fadeOut() }
        ) {
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

        // Register Screen
        composable(
            Routes.REGISTER,
            enterTransition = { slideInVertically() + fadeIn() },
            exitTransition = { slideOutVertically() + fadeOut() }
        ) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                context = LocalContext.current,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        // Main Screen
        composable(
            Routes.MAIN,
            enterTransition = { slideInHorizontally() + fadeIn() },
            exitTransition = { slideOutHorizontally() + fadeOut() }
        ) {
            MainScreen(navController)
        }

        composable(Routes.CALCULADORA) {
            CalculadoraScreen(navController)
        }

        composable(Routes.IMC) {
            ImcScreen(navController)
        }

        composable(Routes.PAM) {
            PamScreen(navController)
        }

        composable(Routes.TECNICAS) {
            AsepsiaScreen(navController)
        }

        composable(Routes.QUIZ_SCREEN) {
            QuizScreen(onDismiss = { navController.popBackStack() })
        }

        composable(Routes.PROCEDIMIENTOS) {
            ProcedScreen(navController)
        }

        composable(Routes.QUIZ_PROCEDIMIENTOS) {
            ProcQuiz(onDismiss = { navController.popBackStack() })
        }

        composable(Routes.QUIZ_PROC_TF) {
            TrueFalseQuizScreen(onDismiss = { navController.popBackStack() })
        }

        composable(Routes.ADMINISTRACION) {
            MedicamentosScreen()
        }

        composable(Routes.URGENCIAS) {
            UrgenciasScreen()
        }
    }
}
