package com.tuapp.zorro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tuapp.zorro.domain.model.Ramo

@Composable
fun RamoChip(ramo: Ramo, modifier: Modifier = Modifier) {
    // Intentamos parsear el color hexadecimal del ramo, si falla usamos un verde "outdoor" por defecto
    val colorBase = try {
        Color(parseColor(ramo.colorHex))
    } catch (e: Exception) {
        Color(0xFF4CAF50) // Verde bosque
    }

    Surface(
        modifier = modifier.padding(4.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        color = colorBase.copy(alpha = 0.15f), // Fondo suave
        border = BorderStroke(1.dp, colorBase) // Borde sólido
    ) {
        Text(
            text = ramo.nombre,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.caption,
            color = colorBase
        )
    }
}

/**
 * Función auxiliar para convertir Hex String a Int de color
 * Útil para mantener la astucia del zorro en el manejo de colores.
 */
fun parseColor(colorString: String): Int {
    if (colorString.startsWith("#")) {
        return colorString.substring(1).toLong(16).toInt() or -0x1000000
    }
    return 0xFF000000.toInt() // Negro por defecto
}