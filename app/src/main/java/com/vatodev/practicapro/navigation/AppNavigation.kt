package com.vatodev.practicapro.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vatodev.practicapro.ui.splash.SplashScreen
import com.vatodev.practicapro.ui.main.MainScreen
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
import com.vatodev.practicapro.ui.user.UserScreen

object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val TECNICAS = "tecnicas"
    const val QUIZ_SCREEN = "quiz_screen"
    const val PROCEDIMIENTOS = "procedimientos"
    const val QUIZ_PROCEDIMIENTOS = "quiz_procedimientos"
    const val QUIZ_PROC_TF = "quiz_proc_tf"
    const val ADMINISTRACION = "administracion"
    const val URGENCIAS = "urgencias"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val IMC = "imc"
    const val PAM = "pam"
    const val USER = "user"

    const val ARG_TECNICA = "tecnica"

    /** Ruta de módulo que abre directamente los pasos de una técnica. */
    fun enTecnica(ruta: String, clave: String) = "$ruta?$ARG_TECNICA=$clave"

    /**
     * Rutas que ocupan toda la pantalla: sin barra inferior. Antes esta lista
     * estaba duplicada en MainActivity y en BottomNavBar.
     */
    val SIN_BARRA = setOf(
        SPLASH, LOGIN, REGISTER,
        QUIZ_SCREEN, QUIZ_PROCEDIMIENTOS, QUIZ_PROC_TF
    )
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
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(
            Routes.REGISTER,
            enterTransition = { slideInVertically() + fadeIn() },
            exitTransition = { slideOutVertically() + fadeOut() }
        ) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(
            Routes.MAIN,
            enterTransition = { slideInHorizontally() + fadeIn() },
            exitTransition = { slideOutHorizontally() + fadeOut() }
        ) {
            MainScreen(navController)
        }


        composable(Routes.IMC) {
            ImcScreen(navController)
        }

        composable(Routes.PAM) {
            PamScreen(navController)
        }

        composable(
            "${Routes.TECNICAS}?${Routes.ARG_TECNICA}={${Routes.ARG_TECNICA}}",
            arguments = listOf(
                navArgument(Routes.ARG_TECNICA) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entrada ->
            val tecnica = entrada.arguments?.getString(Routes.ARG_TECNICA)
            AsepsiaScreen(navController, tecnicaInicial = tecnica)
        }

        composable(Routes.QUIZ_SCREEN) {
            QuizScreen(navController, onDismiss = { navController.popBackStack() })
        }

        composable(
            "${Routes.PROCEDIMIENTOS}?${Routes.ARG_TECNICA}={${Routes.ARG_TECNICA}}",
            arguments = listOf(
                navArgument(Routes.ARG_TECNICA) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entrada ->
            val tecnica = entrada.arguments?.getString(Routes.ARG_TECNICA)
            ProcedScreen(navController, tecnicaInicial = tecnica)
        }

        composable(Routes.QUIZ_PROCEDIMIENTOS) {
            ProcQuiz(navController, onDismiss = { navController.popBackStack() })
        }

        composable(Routes.QUIZ_PROC_TF) {
            TrueFalseQuizScreen(navController, onDismiss = { navController.popBackStack() })
        }

        composable(
            "${Routes.ADMINISTRACION}?${Routes.ARG_TECNICA}={${Routes.ARG_TECNICA}}",
            arguments = listOf(
                navArgument(Routes.ARG_TECNICA) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entrada ->
            val tecnica = entrada.arguments?.getString(Routes.ARG_TECNICA)
            MedicamentosScreen(tecnicaInicial = tecnica)
        }

        composable(
            "${Routes.URGENCIAS}?${Routes.ARG_TECNICA}={${Routes.ARG_TECNICA}}",
            arguments = listOf(
                navArgument(Routes.ARG_TECNICA) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entrada ->
            val tecnica = entrada.arguments?.getString(Routes.ARG_TECNICA)
            UrgenciasScreen(tecnicaInicial = tecnica)
        }

        composable(Routes.USER){
            UserScreen()
        }
    }
}
