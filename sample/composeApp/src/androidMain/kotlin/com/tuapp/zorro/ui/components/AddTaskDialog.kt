package com.tuapp.zorro.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuapp.zorro.domain.model.Ramo
import com.tuapp.zorro.ui.viewmodel.AddTaskViewModel

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
    viewModel: AddTaskViewModel,
    ramos: List<Ramo>
) {
    // Estado local para rastrear qué ramo seleccionó el zorro astuto
    var selectedRamoId by remember {
        mutableStateOf(if (ramos.isNotEmpty()) ramos[0].id else 0L)
    }

    // Observamos el estado del ViewModel de forma reactiva
    val titulo by viewModel.titulo.collectAsState()
    val peso by viewModel.peso.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nueva Misión para el Zorro",
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Define el objetivo y su relevancia académica.",
                    style = MaterialTheme.typography.caption
                )

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { viewModel.onTituloChange(it) }, // Ajustado al nombre de tu función
                    label = { Text("Nombre de la Tarea/Prueba") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = peso,
                    onValueChange = { viewModel.onPesoChange(it) },
                    label = { Text("Peso (%)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Asignar a un Ramo:",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Selector de Ramos usando los Chips que ya creamos
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(ramos) { ramo ->
                        val isSelected = selectedRamoId == ramo.id
                        RamoChip(
                            ramo = ramo,
                            modifier = Modifier.clickable { selectedRamoId = ramo.id }
                        )
                        // Pequeño indicador visual de selección si lo deseas
                        if (isSelected) {
                            // Aquí podrías añadir un borde extra o cambiar el alpha
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedRamoId != 0L) {
                        onConfirm(selectedRamoId)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text("Añadir", color = MaterialTheme.colors.onPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}