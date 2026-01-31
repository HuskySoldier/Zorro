package com.example.acz.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acz.data.local.entity.HorarioEntity
import com.example.acz.data.local.entity.RamoEntity
import com.example.acz.ui.viewmodel.HomeViewModel

@Composable
fun HorarioScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val horario by viewModel.horarioCompleto.collectAsState()
    val ramos by viewModel.ramosDisponibles.collectAsState()

    // 0=Lunes, 4=Viernes. Usamos índices 0-4 para la UI, pero guardamos 1-5 en BD.
    var diaSeleccionadoIndex by remember { mutableIntStateOf(0) } // Empieza en Lunes
    var mostrarDialogo by remember { mutableStateOf(false) }

    val diasTabs = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

    // Filtramos solo las clases del día seleccionado
    val clasesDelDia = horario.filter { it.diaSemana == (diaSeleccionadoIndex + 1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // TABS DE DÍAS
            ScrollableTabRow(
                selectedTabIndex = diaSeleccionadoIndex,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                diasTabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = diaSeleccionadoIndex == index,
                        onClick = { diaSeleccionadoIndex = index },
                        text = { Text(titulo) }
                    )
                }
            }

            // LISTA DE CLASES
            if (clasesDelDia.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Día libre 😎", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(clasesDelDia) { bloque ->
                        // Buscamos el ramo asociado para saber su nombre y color
                        val ramo = ramos.find { it.id == bloque.ramoId }
                        BloqueHorarioItem(bloque, ramo) { viewModel.borrarBloque(bloque) }
                    }
                }
            }
        }

        // FAB AGREGAR
        FloatingActionButton(
            onClick = { mostrarDialogo = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, "Agregar Clase")
        }

        if (mostrarDialogo) {
            if (ramos.isEmpty()) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogo = false },
                    confirmButton = { TextButton(onClick = { mostrarDialogo = false }) { Text("Ok") } },
                    title = { Text("Faltan Ramos") },
                    text = { Text("Primero crea tus ramos en el menú 'Mis Ramos'.") }
                )
            } else {
                AddBloqueDialog(
                    ramos = ramos,
                    diaInicial = diaSeleccionadoIndex + 1,
                    onDismiss = { mostrarDialogo = false },
                    onConfirm = { dia, ini, fin, sala, ramoId ->
                        viewModel.guardarBloque(dia, ini, fin, sala, ramoId)
                        mostrarDialogo = false
                    }
                )
            }
        }
    }
}

@Composable
fun BloqueHorarioItem(bloque: HorarioEntity, ramo: RamoEntity?, onDelete: () -> Unit) {
    val colorRamo = try { Color(android.graphics.Color.parseColor(ramo?.colorHex ?: "#CCCCCC")) } catch (e: Exception) { MaterialTheme.colorScheme.primary }

    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Franja de color a la izquierda
            Box(modifier = Modifier.fillMaxHeight().width(10.dp).background(colorRamo))

            Row(
                modifier = Modifier.weight(1f).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(ramo?.nombre ?: "Ramo desconocido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${bloque.horaInicio} - ${bloque.horaFin}", style = MaterialTheme.typography.bodyLarge)
                    if (!bloque.sala.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(bloque.sala, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBloqueDialog(
    ramos: List<RamoEntity>,
    diaInicial: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String, String, Long) -> Unit
) {
    var ramoSeleccionado by remember { mutableStateOf(ramos.first()) }
    var expandedRamo by remember { mutableStateOf(false) }

    var diaSeleccionado by remember { mutableIntStateOf(diaInicial) } // 1 a 6
    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
    var expandedDia by remember { mutableStateOf(false) }

    var horaInicio by remember { mutableStateOf("08:30") }
    var horaFin by remember { mutableStateOf("10:00") }
    var sala by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Clase") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // SELECTOR DE DÍA
                ExposedDropdownMenuBox(expanded = expandedDia, onExpandedChange = { expandedDia = !expandedDia }) {
                    OutlinedTextField(
                        value = dias.getOrElse(diaSeleccionado - 1) { "Lunes" },
                        onValueChange = {}, readOnly = true, label = { Text("Día") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDia) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedDia, onDismissRequest = { expandedDia = false }) {
                        dias.forEachIndexed { index, dia ->
                            DropdownMenuItem(
                                text = { Text(dia) },
                                onClick = { diaSeleccionado = index + 1; expandedDia = false }
                            )
                        }
                    }
                }

                // SELECTOR DE RAMO
                ExposedDropdownMenuBox(expanded = expandedRamo, onExpandedChange = { expandedRamo = !expandedRamo }) {
                    OutlinedTextField(
                        value = ramoSeleccionado.nombre,
                        onValueChange = {}, readOnly = true, label = { Text("Asignatura") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRamo) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedRamo, onDismissRequest = { expandedRamo = false }) {
                        ramos.forEach { ramo ->
                            DropdownMenuItem(
                                text = { Text(ramo.nombre) },
                                onClick = { ramoSeleccionado = ramo; expandedRamo = false }
                            )
                        }
                    }
                }

                // HORAS (Simple Textos por ahora)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = horaInicio, onValueChange = { horaInicio = it }, label = { Text("Inicio") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = horaFin, onValueChange = { horaFin = it }, label = { Text("Fin") }, modifier = Modifier.weight(1f))
                }

                OutlinedTextField(value = sala, onValueChange = { sala = it }, label = { Text("Sala (Opcional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(diaSeleccionado, horaInicio, horaFin, sala, ramoSeleccionado.id) }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}