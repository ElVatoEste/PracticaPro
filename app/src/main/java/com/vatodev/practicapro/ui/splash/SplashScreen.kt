package com.vatodev.practicapro.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import kotlinx.coroutines.delay
import com.vatodev.practicapro.R

@Composable
fun SplashScreen(navController: NavController, margin: Int = 8) {
    // Animaciones para opacidad y escala
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animaciones de SplashScreen
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        )
        delay(1000)

        // Verificar el estado del usuario
        val userDao = DatabaseProvider.getDatabase(navController.context).userDao()
        val user = userDao.getUser()
        val isLoggedIn = user != null && user.expirationDate > System.currentTimeMillis()

        // Redirigir según el estado del usuario
        navController.navigate(if (isLoggedIn) "main" else "login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Contenido visual del Splash
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Fondo blanco
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_fm),
            contentDescription = "Logo UAM Facultad Medicina",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = margin.dp)
                .aspectRatio(2f, matchHeightConstraintsFirst = false)
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}
