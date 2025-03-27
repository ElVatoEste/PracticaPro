package com.vatodev.practicapro.ui.navbar

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.repository.AuthRepository
import com.vatodev.practicapro.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun BottomNavigationBar(navController: NavController, userViewModel: UserViewModel) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    val excludedRoutes = setOf(
        Routes.LOGIN,
        Routes.SPLASH,
        Routes.REGISTER,
        Routes.QUIZ_PROCEDIMIENTOS,
        Routes.QUIZ_PROC_TF,
        Routes.QUIZ_SCREEN
    )

    if (currentRoute !in excludedRoutes ) {
        Surface(
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = Color(0xFF7DBB00),
            shadowElevation = 6.dp
        ) {
            NavigationBar(
                modifier = Modifier.height(52.dp),
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
                            tint = if (currentRoute == Routes.MAIN) Color.White else Color(
                                0xFFFFFFFF
                            )
                        )
                    },
                    label = {
                        Text(
                            text = "Inicio",
                            fontSize = 13.sp,
                            color = if (currentRoute == Routes.MAIN) Color.White else Color(
                                0xFFFFFFFF
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF4CAF50)
                    )
                )

                // Botón Usuario (CENTRO)
                NavigationBarItem(
                    selected = currentRoute == Routes.USER,
                    onClick = {
                        navController.navigate(Routes.USER)
                    },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Usuario",
                            tint = if (currentRoute == Routes.USER) Color.White else Color(0xFFFFFFFF)
                        )
                    },
                    label = {
                        Text(
                            text = "Usuario",
                            fontSize = 13.sp,
                            color = if (currentRoute == Routes.USER) Color.White else Color(0xFFFFFFFF)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF4CAF50)
                    )
                )

                // Botón Logout
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        scope.launch {
                            AuthRepository.logout(navController.context, userViewModel)
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.MAIN) { inclusive = true }
                            }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = Color(0xFFFFFFFF)
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

@Preview(showBackground = true)
@Composable
fun PreviewBottomNavigationBar() {
    val navController = rememberNavController()
    BottomNavigationBar(navController = navController, userViewModel = UserViewModel())
}
