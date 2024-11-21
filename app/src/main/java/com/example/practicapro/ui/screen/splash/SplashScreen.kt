package com.example.practicapro.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.practicapro.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    margin: Int = 8 // Margen configurable en dp
) {
    // Retardo antes de navegar
    LaunchedEffect(Unit) {
        delay(3000) // 3 segundos
        navController.navigate("main") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Contenido visual de la Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_fm),
            contentDescription = "Logo UAM Facultad Medicina",
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = margin.dp)
                .aspectRatio(2f, matchHeightConstraintsFirst = false)
        )
    }
}

// Preview para mostrar cómo se verá la pantalla Splash
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    val mockNavController = rememberNavController()
    SplashScreen(navController = mockNavController, margin = 8) // Margen configurado en el preview
}
