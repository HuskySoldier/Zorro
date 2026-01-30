package com.tuapp.zorro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuapp.zorro.domain.model.Tarea

@Composable
fun TareaCard(tarea: Tarea, prioridad: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información de la Tarea
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Peso académico: ${tarea.peso}%",
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Indicador de Prioridad del Zorro
            val colorPrioridad = obtenerColorPrioridad(prioridad)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorPrioridad.copy(alpha = 0.2f)) // Fondo suave
            ) {
                // El número de prioridad con el color sólido
                Text(
                    text = formatearPrioridad(prioridad),
                    color = colorPrioridad,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

/**
 * Paleta Outdoor: Mapea la urgencia a colores de la naturaleza.
 * De Verde Bosque (Baja) a Tierra Roja (Crítica).
 */
fun obtenerColorPrioridad(prioridad: Double): Color {
    return when {
        prioridad < 5.0 -> Color(0xFF2E7D32)  // Verde (Bajo impacto)
        prioridad < 15.0 -> Color(0xFFFBC02D) // Amarillo (Atención)
        prioridad < 30.0 -> Color(0xFFE65100) // Naranja (Urgente)
        else -> Color(0xFFB71C1C)             // Rojo (Crítico)
    }
}

/**
 * Formateo simple para evitar problemas de compatibilidad en KMP commonMain
 */
private fun formatearPrioridad(valor: Double): String {
    val entero = valor.toInt()
    val decimal = ((valor - entero) * 10).toInt()
    return "$entero.$decimal"
}