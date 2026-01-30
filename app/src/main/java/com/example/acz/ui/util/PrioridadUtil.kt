package com.example.acz.ui.util

import androidx.compose.ui.graphics.Color
import java.util.concurrent.TimeUnit

// Definimos los colores semánticos
val ColorUrgente = Color(0xFFFF5252) // Rojo
val ColorAtento = Color(0xFFFFD740)  // Amarillo/Dorado
val ColorRelax = Color(0xFF69F0AE)   // Verde

fun calcularColorPrioridad(fechaEntrega: Long, peso: Int): Color {
    val hoy = System.currentTimeMillis()
    val diffMs = fechaEntrega - hoy
    // Convertimos milisegundos a días. Si ya pasó o es hoy, ponemos 0.
    val diasRestantes = TimeUnit.MILLISECONDS.toDays(diffMs).coerceAtLeast(0)

    return when {
        diasRestantes <= 2 -> ColorUrgente // ¡Faltan 2 días o menos!
        diasRestantes <= 7 && peso > 20 -> ColorUrgente // Falta una semana pero vale mucho
        diasRestantes <= 7 -> ColorAtento // Falta una semana, peso normal
        else -> ColorRelax // Falta mucho
    }
}