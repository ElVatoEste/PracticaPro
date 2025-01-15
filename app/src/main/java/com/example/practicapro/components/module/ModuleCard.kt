package com.example.practicapro.components.module

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Modelo de datos para un módulo
data class Module(val name: String, val description: String, val imageRes: Int)

// Composable para las tarjetas de módulos
@Composable
fun ModuleCard(module: Module, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick, // Habilita la funcionalidad clicable
        modifier = modifier
            .height(240.dp), // Altura ajustada
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5) // Fondo claro
        ),
        elevation = CardDefaults.cardElevation(8.dp), // Efecto de elevación
        shape = RoundedCornerShape(12.dp) // Esquinas redondeadas
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen que ocupa la parte superior
            Image(
                painter = painterResource(id = module.imageRes),
                contentDescription = module.name,
                contentScale = ContentScale.Crop, // La imagen llena el espacio
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp) // Imagen cuadrada
            )

            // Contenedor para título y descripción
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // La sección inferior ocupa el espacio restante
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.SpaceEvenly // Espaciado uniforme entre título y descripción
            ) {
                // Título ocupa la mitad del espacio
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = module.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                // Descripción ocupa la otra mitad
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = module.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
