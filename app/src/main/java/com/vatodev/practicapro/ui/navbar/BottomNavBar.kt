package com.vatodev.practicapro.ui.navbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.network.BackendGate
import com.vatodev.practicapro.repository.AuthRepository
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado
import com.vatodev.practicapro.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun BottomNavigationBar(navController: NavController, userViewModel: UserViewModel) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()
    val estado = LocalEstado.current

    if (currentRoute in Routes.SIN_BARRA) return

    Column {
        Filete()
        NavigationBar(
            modifier = Modifier.height(64.dp),
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            Destino(
                icono = Icons.Default.Home,
                etiqueta = "Inicio",
                seleccionado = currentRoute == Routes.MAIN,
                activo = estado.progreso,
                inactivo = estado.textoSuave,
                onClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
            Destino(
                icono = Icons.Default.Person,
                etiqueta = "Perfil",
                seleccionado = currentRoute == Routes.USER,
                activo = estado.progreso,
                inactivo = estado.textoSuave,
                onClick = { navController.navigate(Routes.USER) }
            )
            Destino(
                icono = Icons.AutoMirrored.Filled.Logout,
                etiqueta = "Salir",
                seleccionado = false,
                activo = estado.progreso,
                inactivo = estado.textoSuave,
                onClick = {
                    scope.launch {
                        AuthRepository.logout(navController.context, userViewModel)
                        val destino = if (BackendGate.isEnabled) Routes.LOGIN else Routes.REGISTER
                        navController.navigate(destino) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.Destino(
    icono: ImageVector,
    etiqueta: String,
    seleccionado: Boolean,
    activo: Color,
    inactivo: Color,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = seleccionado,
        onClick = onClick,
        icon = { Icon(icono, contentDescription = etiqueta) },
        label = { Text(etiqueta.uppercase(), style = EtiquetaTracked.copy(fontSize = 11.sp)) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = activo,
            selectedTextColor = activo,
            unselectedIconColor = inactivo,
            unselectedTextColor = inactivo,
            indicatorColor = Color.Transparent
        )
    )
}

