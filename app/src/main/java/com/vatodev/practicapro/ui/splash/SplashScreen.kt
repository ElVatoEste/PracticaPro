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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.repository.AuthRepository
import com.vatodev.practicapro.repository.SesionRepository
import kotlinx.coroutines.delay
import com.vatodev.practicapro.R

@Composable
fun SplashScreen(navController: NavController, margin: Int = 8) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        )
        delay(1000)

        val contexto = navController.context
        val destination = when {
            SesionRepository.usuario(contexto) != null -> Routes.MAIN
            SesionRepository.adoptarCuentaHeredada(contexto) -> Routes.MAIN
            AuthRepository.hayCuentas(contexto) -> Routes.LOGIN
            else -> Routes.REGISTER
        }
        navController.navigate(destination) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
