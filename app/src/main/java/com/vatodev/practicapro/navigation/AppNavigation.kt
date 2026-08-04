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
import androidx.navigation.compose.composable
import com.vatodev.practicapro.ui.splash.SplashScreen
import com.vatodev.practicapro.ui.main.MainScreen
import com.vatodev.practicapro.ui.calculadora.ImcScreen
import com.vatodev.practicapro.ui.calculadora.PamScreen
import com.vatodev.practicapro.network.BackendGate
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

        if (BackendGate.isEnabled) {
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
                    context = navController.context,
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                )
            }
        }

        composable(
            Routes.REGISTER,
            enterTransition = { slideInVertically() + fadeIn() },
            exitTransition = { slideOutVertically() + fadeOut() }
        ) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                context = navController.context
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

        composable(Routes.TECNICAS) {
            AsepsiaScreen(navController)
        }

        composable(Routes.QUIZ_SCREEN) {
            QuizScreen(navController, onDismiss = { navController.popBackStack() })
        }

        composable(Routes.PROCEDIMIENTOS) {
            ProcedScreen(navController)
        }

        composable(Routes.QUIZ_PROCEDIMIENTOS) {
            ProcQuiz(navController, onDismiss = { navController.popBackStack() })
        }

        composable(Routes.QUIZ_PROC_TF) {
            TrueFalseQuizScreen(navController, onDismiss = { navController.popBackStack() })
        }

        composable(Routes.ADMINISTRACION) {
            MedicamentosScreen()
        }

        composable(Routes.URGENCIAS) {
            UrgenciasScreen()
        }

        composable(Routes.USER){
            UserScreen()
        }
    }
}
