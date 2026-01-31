package com.example.acz.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acz.data.local.entity.NotaEntity
import com.example.acz.data.local.entity.RamoEntity
import com.example.acz.ui.viewmodel.HomeViewModel

@Composable
fun NotasScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val ramos by viewModel.ramosDisponibles.collectAsState()
    var ramoSeleccionado by remember { mutableStateOf<RamoEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (ramos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Agrega ramos en el menú 'Mis Ramos' primero.")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Resumen de Notas", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(ramos) { ramo ->
                    RamoPromedioCard(ramo = ramo, viewModel = viewModel, onClick = { ramoSeleccionado = ramo })
                }
            }
        }

        if (ramoSeleccionado != null) {
            DetalleNotasDialog(
                ramo = ramoSeleccionado!!,
                viewModel = viewModel,
                onDismiss = { ramoSeleccionado = null }
            )
        }
    }
}

@Composable
fun RamoPromedioCard(ramo: RamoEntity, viewModel: HomeViewModel, onClick: () -> Unit) {
    val notas by viewModel.obtenerNotas(ramo.id).collectAsState(initial = emptyList())

    // Promedio Ponderado de Presentación
    val promedio = remember(notas) {
        if (notas.isEmpty()) 0.0 else {
            val sumaPond = notas.sumOf { it.valor * it.porcentaje }
            val sumaPorc = notas.sumOf { it.porcentaje }
            if (sumaPorc > 0) sumaPond / sumaPorc else 0.0
        }
    }

    val colorRamo = try { Color(android.graphics.Color.parseColor(ramo.colorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }

    Card(onClick = onClick, elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(colorRamo))
                Spacer(modifier = Modifier.width(12.dp))
                Text(ramo.nombre, style = MaterialTheme.typography.titleMedium)
            }
            Surface(
                color = if(promedio < 4.0 && notas.isNotEmpty()) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if(notas.isEmpty()) "--" else String.format("%.1f", promedio),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = if(promedio < 4.0 && notas.isNotEmpty()) Color(0xFFC62828) else Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
fun DetalleNotasDialog(ramo: RamoEntity, viewModel: HomeViewModel, onDismiss: () -> Unit) {
    val notas by viewModel.obtenerNotas(ramo.id).collectAsState(initial = emptyList())
    var notaParaEditar by remember { mutableStateOf<NotaEntity?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    // Calcular el promedio actual (Nota de Presentación)
    val promedioPresentacion = remember(notas) {
        if (notas.isEmpty()) 0.0 else {
            val sumaPond = notas.sumOf { it.valor * it.porcentaje }
            val sumaPorc = notas.sumOf { it.porcentaje }
            if (sumaPorc > 0) sumaPond / sumaPorc else 0.0
        }
    }

    // Porcentaje de evaluación completado del semestre (ej: llevamos el 60% evaluado)
    val avanceSemestre = notas.sumOf { it.porcentaje }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(min = 500.dp, max = 800.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Notas de ${ramo.nombre}", style = MaterialTheme.typography.titleLarge)

                // Mostrar Promedio de Presentación
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Nota Presentación: ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = String.format("%.1f", promedioPresentacion),
                        fontWeight = FontWeight.Bold,
                        color = if (promedioPresentacion < 4.0) Color.Red else Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("(${avanceSemestre}% evaluado)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // LISTA DE NOTAS
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(notas) { nota ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(nota.nombre, style = MaterialTheme.typography.bodyMedium)
                                Text("${nota.porcentaje}%", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = nota.valor.toString(),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp),
                                    color = if (nota.valor < 4.0) Color.Red else Color(0xFF388E3C)
                                )
                                IconButton(onClick = {
                                    notaParaEditar = nota
                                    mostrarFormulario = true
                                }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.borrarNota(nota) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }

                Button(
                    onClick = {
                        notaParaEditar = null
                        mostrarFormulario = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Nota")
                }

                // --- SIMULADOR DE EXAMEN CORREGIDO ---
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                // Solo mostramos el simulador si hay al menos una nota puesta
                if (notas.isNotEmpty()) {
                    SimuladorExamenReal(promedioPresentacion = promedioPresentacion)
                }
            }
        }
    }

    if (mostrarFormulario) {
        AddEditNotaDialog(
            notaAEditar = notaParaEditar,
            onDismiss = { mostrarFormulario = false },
            onConfirm = { nombre, valor, porcentaje ->
                if (notaParaEditar != null) {
                    val notaActualizada = notaParaEditar!!.copy(nombre = nombre, valor = valor, porcentaje = porcentaje)
                    viewModel.actualizarNota(notaActualizada)
                } else {
                    viewModel.guardarNota(nombre, valor, porcentaje, ramo.id)
                }
                mostrarFormulario = false
            }
        )
    }
}

// --- EL SIMULADOR DE EXAMEN DEFINITIVO ---
@Composable
fun SimuladorExamenReal(promedioPresentacion: Double) {
    // Estado del porcentaje del examen (Ej: 30%)
    var porcentajeExamenStr by remember { mutableStateOf("30") }
    // Estado de la nota final deseada (Ej: 4.0 para aprobar)
    var notaFinalDeseadaStr by remember { mutableStateOf("4.0") }

    val porcentajeExamen = porcentajeExamenStr.toDoubleOrNull() ?: 30.0
    val notaFinalDeseada = notaFinalDeseadaStr.toDoubleOrNull() ?: 4.0

    // CÁLCULO MATEMÁTICO:
    // NotaFinal = (Pres * %Pres) + (Examen * %Examen)
    // Despejamos Examen:
    // Examen = (NotaFinal - (Pres * %Pres)) / %Examen

    val porcentajePresentacionDecimal = (100 - porcentajeExamen) / 100.0
    val porcentajeExamenDecimal = porcentajeExamen / 100.0

    val puntosPresentacion = promedioPresentacion * porcentajePresentacionDecimal

    val notaExamenNecesaria = if (porcentajeExamenDecimal > 0) {
        (notaFinalDeseada - puntosPresentacion) / porcentajeExamenDecimal
    } else {
        0.0
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Simular Examen Final", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Input: Nota Final que quieres lograr
                OutlinedTextField(
                    value = notaFinalDeseadaStr,
                    onValueChange = { notaFinalDeseadaStr = it },
                    label = { Text("Meta Final") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                // Input: Cuánto vale el examen
                OutlinedTextField(
                    value = porcentajeExamenStr,
                    onValueChange = { porcentajeExamenStr = it },
                    label = { Text("% Examen") },
                    modifier = Modifier.weight(0.8f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lógica de visualización del resultado
            val textoResultado = when {
                porcentajeExamen <= 0 || porcentajeExamen >= 100 -> "Revisa el % del examen"
                notaExamenNecesaria > 7.0 -> "Imposible 💀 (Necesitas ${String.format("%.1f", notaExamenNecesaria)})"
                notaExamenNecesaria <= 1.0 -> "¡Aprobado! 🎉 (Te sobra nota)"
                else -> "Necesitas un ${String.format("%.1f", notaExamenNecesaria)} en el examen"
            }

            val colorResultado = when {
                notaExamenNecesaria > 7.0 -> MaterialTheme.colorScheme.error
                notaExamenNecesaria <= 4.0 -> Color(0xFF2E7D32) // Verde
                else -> MaterialTheme.colorScheme.primary
            }

            Text(
                text = textoResultado,
                style = MaterialTheme.typography.titleMedium, // Un poco más grande
                color = colorResultado,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Texto explicativo pequeño
            if (porcentajeExamen > 0 && porcentajeExamen < 100) {
                Text(
                    text = "Tu ${String.format("%.1f", promedioPresentacion)} vale el ${100-porcentajeExamen.toInt()}% de la nota final.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AddEditNotaDialog(
    notaAEditar: NotaEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Int) -> Unit
) {
    var nombre by remember { mutableStateOf(notaAEditar?.nombre ?: "") }
    var valorStr by remember { mutableStateOf(notaAEditar?.valor?.toString() ?: "") }
    var porcStr by remember { mutableStateOf(notaAEditar?.porcentaje?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (notaAEditar != null) "Editar Nota" else "Nueva Nota") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre (Ej: Solemne 1)") }, singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = valorStr, onValueChange = { valorStr = it },
                        label = { Text("Nota") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = porcStr, onValueChange = { if(it.length <= 3) porcStr = it },
                        label = { Text("%") }, modifier = Modifier.weight(0.6f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val v = valorStr.toDoubleOrNull()
                val p = porcStr.toIntOrNull()
                val n = if (nombre.isBlank()) "Evaluación" else nombre
                if (v != null && p != null) onConfirm(n, v, p)
            }, enabled = valorStr.isNotBlank() && porcStr.isNotBlank()) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}