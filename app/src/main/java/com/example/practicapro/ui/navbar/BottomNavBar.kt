package com.example.practicapro.ui.navbar

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.practicapro.navigation.Routes
import com.example.practicapro.rooms.appDatabase.DatabaseProvider
import kotlinx.coroutines.launch

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    if (currentRoute != Routes.LOGIN && currentRoute != Routes.SPLASH) {
        Surface(
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = Color(0xFF7DBB00),
            shadowElevation = 6.dp
        ) {
            NavigationBar(
                modifier = Modifier.height(48.dp),
                containerColor = Color.Transparent,
                contentColor = Color.White
            ) {
                // Botón Home
                NavigationBarItem(
                    selected = currentRoute == Routes.MAIN,
                    onClick = {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Inicio",
                            tint = if (currentRoute == Routes.MAIN) Color.White else Color(0xFF000000)
                        )
                    },
                    label = {
                        Text(
                            text = "Inicio",
                            fontSize = 13.sp,
                            color = if (currentRoute == Routes.MAIN) Color.White else Color(0xFF000000)
                        )
                    }
                )

                // Botón Logout
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        scope.launch {
                            val userDao = DatabaseProvider.getDatabase(navController.context).userDao()
                            userDao.deleteUser() // Eliminar usuario de la base de datos
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.MAIN) { inclusive = true }
                            }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = Color(0xFF7DBB00)
                        )
                    },
                    label = {
                        Text(
                            text = "Cerrar Sesión",
                            fontSize = 13.sp,
                            color = Color(0xFFF8F8F8)
                        )
                    }
                )
            }
        }
    }
}
