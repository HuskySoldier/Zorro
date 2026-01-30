package com.example.acz.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.acz.data.local.entity.RamoEntity
import com.example.acz.data.local.entity.TareaEntity // Importar TareaEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTareaDialog(
    ramos: List<RamoEntity>,
    ramoInicial: RamoEntity? = null,
    tareaAEditar: TareaEntity? = null, // <--- NUEVO PARAMETRO: Tarea para editar
    onDismiss: () -> Unit,
    // Callback modificado: Devuelve el ID (si existe) y el estado original
    onConfirm: (Long?, String, String, Int, Long, Long, String) -> Unit
) {
    // Si estamos editando, usamos los valores de la tarea. Si no, valores por defecto.
    // Usamos 'remember(tareaAEditar)' para reiniciar estos valores si cambiamos de tarea.
    var titulo by remember(tareaAEditar) { mutableStateOf(tareaAEditar?.titulo ?: "") }
    var pesoStr by remember(tareaAEditar) { mutableStateOf(tareaAEditar?.peso?.toString() ?: "30") }
    var tipoSeleccionado by remember(tareaAEditar) { mutableStateOf(tareaAEditar?.tipo ?: "PRUEBA") }

    // Fecha: Si editamos, la de la tarea. Si no, mañana.
    val fechaInicial = tareaAEditar?.fechaEntrega ?: (System.currentTimeMillis() + 86400000)

    // Ramo: Si editamos, buscamos el ramo de la tarea.
    val ramoDeLaTarea = ramos.find { it.id == tareaAEditar?.ramoId }
    var ramoSeleccionado by remember(tareaAEditar, ramoInicial) {
        mutableStateOf(ramoDeLaTarea ?: ramoInicial ?: ramos.firstOrNull())
    }

    // ... (El resto de lógica de fecha y menú sigue igual) ...
    var mostrarMenuRamos by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = fechaInicial)
    val fechaSeleccionada = datePickerState.selectedDateMillis ?: fechaInicial
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = { TextButton(onClick = { mostrarCalendario = false }) { Text("Aceptar") } },
            dismissButton = { TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tareaAEditar != null) "Editar Tarea" else "Nueva Entrega") }, // Título dinámico
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titulo, onValueChange = { titulo = it },
                    label = { Text("Título") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )

                // SELECTOR DE RAMO
                if (ramos.isNotEmpty()) {
                    Box {
                        OutlinedTextField(
                            value = ramoSeleccionado?.nombre ?: "Selecciona un ramo",
                            onValueChange = {}, readOnly = true, label = { Text("Asignatura") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "ver") },
                            modifier = Modifier.fillMaxWidth().clickable { mostrarMenuRamos = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { mostrarMenuRamos = true })
                        DropdownMenu(expanded = mostrarMenuRamos, onDismissRequest = { mostrarMenuRamos = false }) {
                            ramos.forEach { ramo ->
                                DropdownMenuItem(
                                    text = { Text(ramo.nombre) },
                                    onClick = { ramoSeleccionado = ramo; mostrarMenuRamos = false }
                                )
                            }
                        }
                    }
                } else {
                    Text("⚠️ Crea un Ramo primero", color = MaterialTheme.colorScheme.error)
                }

                // TIPO
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = { tipoSeleccionado = "PRUEBA" }, label = { Text("Prueba") },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = if(tipoSeleccionado == "PRUEBA") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
                    )
                    SuggestionChip(
                        onClick = { tipoSeleccionado = "TRABAJO" }, label = { Text("Trabajo") },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = if(tipoSeleccionado == "TRABAJO") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
                    )
                }

                OutlinedTextField(
                    value = pesoStr, onValueChange = { if (it.all { char -> char.isDigit() }) pesoStr = it },
                    label = { Text("Peso (%)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sdf.format(Date(fechaSeleccionada)), onValueChange = {}, readOnly = true,
                    label = { Text("Fecha") }, trailingIcon = { Icon(Icons.Default.DateRange, "") },
                    modifier = Modifier.fillMaxWidth().clickable { mostrarCalendario = true }, enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                    )
                )
                Box(modifier = Modifier.fillMaxWidth().height(56.dp).offset(y = (-56).dp).clickable { mostrarCalendario = true })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotEmpty() && ramoSeleccionado != null) {
                        onConfirm(
                            tareaAEditar?.id, // Pasamos el ID si existe
                            titulo,
                            tipoSeleccionado,
                            pesoStr.toIntOrNull() ?: 0,
                            fechaSeleccionada,
                            ramoSeleccionado!!.id,
                            tareaAEditar?.estado ?: "PENDIENTE" // Mantenemos el estado original
                        )
                        onDismiss()
                    }
                },
                enabled = titulo.isNotEmpty() && ramoSeleccionado != null
            ) { Text(if (tareaAEditar != null) "Actualizar" else "Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}