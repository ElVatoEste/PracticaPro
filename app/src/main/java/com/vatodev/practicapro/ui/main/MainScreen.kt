package com.vatodev.practicapro.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vatodev.practicapro.components.general.Etiqueta
import com.vatodev.practicapro.components.general.Filete
import com.vatodev.practicapro.components.general.Intentos
import com.vatodev.practicapro.model.MODULOS
import com.vatodev.practicapro.model.Modulo
import com.vatodev.practicapro.navigation.Routes
import com.vatodev.practicapro.network.BackendGate
import com.vatodev.practicapro.ui.theme.Dato
import com.vatodev.practicapro.ui.theme.EtiquetaTracked
import com.vatodev.practicapro.ui.theme.LocalEstado
import com.vatodev.practicapro.viewmodel.EstadoModulo
import com.vatodev.practicapro.viewmodel.InicioViewModel

@Composable
fun MainScreen(
    navController: NavController,
    inicioViewModel: InicioViewModel = viewModel()
) {
    val context = LocalContext.current
    val resumen by inicioViewModel.resumen

    LaunchedEffect(navController.currentBackStackEntry) {
        inicioViewModel.cargar(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Encabezado()

        Spacer(Modifier.height(26.dp))
        BloqueResumen(
            modulos = resumen.modulos.toString(),
            intentos = "${resumen.intentosUsados}/${resumen.intentosTotales}",
            promedio = resumen.promedio?.toString() ?: "—"
        )

        Spacer(Modifier.height(26.dp))
        Etiqueta("Calculadoras clínicas")
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaHerramienta(
                nombre = "IMC",
                descripcion = "Índice de masa corporal",
                icono = Icons.Default.Scale,
                colorIcono = LocalEstado.current.progreso,
                onClick = { navController.navigate(Routes.IMC) },
                modifier = Modifier.weight(1f)
            )
            TarjetaHerramienta(
                nombre = "PAM",
                descripcion = "Presión arterial media",
                icono = Icons.Default.MonitorHeart,
                colorIcono = LocalEstado.current.logro,
                onClick = { navController.navigate(Routes.PAM) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(26.dp))
        Etiqueta("Módulos de estudio")
        Spacer(Modifier.height(14.dp))
        MODULOS.forEach { modulo ->
            Filete()
            FilaModulo(
                modulo = modulo,
                estado = resumen.porModulo[modulo.subjectId],
                onClick = { navController.navigate(modulo.ruta) }
            )
        }
        Filete()
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun Encabezado() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "PRACTICAPRO",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 17.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(LocalEstado.current.progreso)
            )
            Spacer(Modifier.size(7.dp))
            Text(
                text = if (BackendGate.isEnabled) "EN LÍNEA" else "OFFLINE",
                style = EtiquetaTracked.copy(fontSize = 13.sp, letterSpacing = 1.6.sp),
                color = LocalEstado.current.textoSuave
            )
        }
    }
}

@Composable
private fun BloqueResumen(modulos: String, intentos: String, promedio: String) {
    val estado = LocalEstado.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(estado.elevado)
            .padding(18.dp)
    ) {
        listOf(
            Triple("Módulos", modulos, MaterialTheme.colorScheme.onSurface),
            Triple("Intentos", intentos, estado.progreso),
            Triple("Promedio", promedio, estado.logro)
        ).forEach { (clave, valor, color) ->
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Etiqueta(clave)
                Text(
                    text = valor,
                    style = Dato.copy(fontSize = 26.sp, letterSpacing = (-0.5).sp),
                    color = color
                )
            }
        }
    }
}

@Composable
private fun TarjetaHerramienta(
    nombre: String,
    descripcion: String,
    icono: ImageVector,
    colorIcono: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado = LocalEstado.current
    Column(
        modifier = modifier
            .background(estado.elevado)
            .border(1.dp, estado.filete, RectangleShape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(20.dp))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = estado.textoSuave,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = nombre,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 22.sp, letterSpacing = 2.2.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = estado.textoSuave
        )
    }
}

@Composable
private fun FilaModulo(modulo: Modulo, estado: EstadoModulo?, onClick: () -> Unit) {
    val colores = LocalEstado.current
    val intentos = estado?.intentos ?: 0
    val agotado = intentos >= modulo.maxIntentos

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = modulo.indice,
            style = Dato.copy(fontSize = 13.sp),
            color = if (intentos > 0) colores.progreso else colores.textoSuave
        )
        Image(
            painter = painterResource(modulo.imagen),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(56.dp)
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = modulo.nombre,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Intentos(consumidos = intentos, total = modulo.maxIntentos)
                Spacer(Modifier.size(8.dp))
                Text(
                    text = textoEstado(intentos, modulo.maxIntentos, agotado, estado?.mejorNota),
                    style = Dato.copy(fontSize = 11.sp),
                    color = colores.textoSuave
                )
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = colores.textoSuave,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun textoEstado(intentos: Int, maximo: Int, agotado: Boolean, mejor: Int?): String = when {
    intentos == 0 -> "Sin iniciar"
    agotado && mejor != null -> "Mejor nota $mejor"
    agotado -> "Sin intentos"
    else -> "$intentos de $maximo intentos"
}
