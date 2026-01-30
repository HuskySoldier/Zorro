package com.tuapp.zorro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tuapp.zorro.ui.viewmodel.AddSubjectViewModel

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    viewModel: AddSubjectViewModel
) {
    val nombreRamo by viewModel.nombreRamo.collectAsState()
    val colorSeleccionado by viewModel.colorSeleccionado.collectAsState()

    val coloresOutdoor = listOf(
        "#2E7D32",
        "#1B5E20",
        "#3E2723",
        "#304741",
        "#006064"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Nueva Materia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = nombreRamo,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre del Ramo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Color de identificación:",
                    style = MaterialTheme.typography.caption
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    coloresOutdoor.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(hexToColor(hex))
                                .clickable { viewModel.onColorChange(hex) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.guardarRamo()
                    onDismiss()
                }
            ) {
                Text("Confirmar")
            }
        }
    )
}

/**
 * Convierte un color HEX (#RRGGBB o #AARRGGBB) a Color de Compose
 */
private fun hexToColor(colorString: String): Color {
    return try {
        val hex = colorString.removePrefix("#")
        val longValue = hex.toLong(16)

        if (hex.length == 6) {
            Color(longValue or 0xFF000000L) // Opacidad completa
        } else {
            Color(longValue)
        }
    } catch (e: Exception) {
        Color.Black
    }
}
