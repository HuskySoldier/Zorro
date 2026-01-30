package com.example.acz.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardCard(
    totalTareas: Int,
    tareasCompletadas: Int
) {
    // Cálculo matemático del progreso (evitando división por cero)
    val progreso = if (totalTareas > 0) tareasCompletadas.toFloat() / totalTareas else 0f
    val porcentaje = (progreso * 100).toInt()

    // Animación suave de la barra
    val progresoAnimado by animateFloatAsState(targetValue = progreso, label = "progreso")

    // Mensaje dinámico según el avance
    val mensaje = when {
        totalTareas == 0 -> "¡Bienvenido! Agrega tu primera tarea."
        progreso == 1f -> "¡Eres una máquina! Semestre al día. 🎉"
        progreso > 0.75f -> "¡Ya casi terminas! Último esfuerzo. 🔥"
        progreso > 0.5f -> "Vas por la mitad, ¡sigue así! 🚀"
        progreso > 0.25f -> "Buen ritmo, no te detengas. 💪"
        else -> "El camino es largo, pero tú puedes. 🧗"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Margen externo
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp) // Margen interno
        ) {
            Text(
                text = "Resumen del Semestre",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$porcentaje%",
                    style = MaterialTheme.typography.displayMedium, // Texto gigante
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$tareasCompletadas de $totalTareas tareas",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de Progreso
            LinearProgressIndicator(
                progress = { progresoAnimado },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}