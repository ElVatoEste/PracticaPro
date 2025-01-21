package com.vatodev.practicapro.ui.study.asepsia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vatodev.practicapro.R
import com.vatodev.practicapro.components.TechniqueCard


@Composable
fun TechniqueCardList(onCardClick: (String, List<String>) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TechniqueCard(
            title = "Lavado de Manos Clínico",
            description = "Pasos para un lavado de manos correcto.",
            imageRes = R.drawable.ic_asepsia3,
            onClick = {
                onCardClick("Lavado de Manos Clínico", stepsLavadoClinico)
            }
        )
        TechniqueCard(
            title = "Lavado de Manos Quirúrgico",
            description = "Elimina la flora transitoria y reduce al máximo la flora residente de las manos, previo a procedimientos invasivos.",
            imageRes = R.drawable.ic_asepsia2,
            onClick = {
                onCardClick("Lavado de Manos Quirúrgico", stepsLavadoQuirurgico)
            }
        )
        TechniqueCard(
            title = "Uso de Guantes",
            description = "Conoce el uso correcto del equipo de protección personal.",
            imageRes = R.drawable.ic_asepsia4,
            onClick = {
                onCardClick("Uso de Guantes", stepsUsoGuantes)
            }
        )
    }
}