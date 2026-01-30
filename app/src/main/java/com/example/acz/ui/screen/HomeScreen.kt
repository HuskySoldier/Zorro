package com.example.acz.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit // <--- IMPORTANTE: Icono de editar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acz.data.local.entity.TareaEntity
import com.example.acz.ui.viewmodel.HomeViewModel
import com.example.acz.ui.util.calcularColorPrioridad
import com.example.acz.ui.component.DashboardCard
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    // ESTADOS DE DATOS
    val pendientes by viewModel.tareasPendientes.collectAsState()
    val completadas by viewModel.tareasCompletadas.collectAsState()
    val ramos by viewModel.ramosDisponibles.collectAsState()

    // ESTADOS DE UI
    var mostrarDialogo by remember { mutableStateOf(false) }
    var tabIndex by remember { mutableIntStateOf(0) } // 0 = Pendientes, 1 = Historial
    var filtroRamoId by remember { mutableStateOf<Long?>(null) }

    // --- NUEVO: ESTADO PARA CONTROLAR LA EDICIÓN ---
    var tareaParaEditar by remember { mutableStateOf<TareaEntity?>(null) }
    val mostrarDialogoEdicion = tareaParaEditar != null

    // CÁLCULOS DASHBOARD
    val totalTareas = pendientes.size + completadas.size
    val cantidadCompletadas = completadas.size

    // FILTRADO
    val pendientesFiltradas = if (filtroRamoId == null) pendientes else pendientes.filter { it.ramoId == filtroRamoId }
    val completadasFiltradas = if (filtroRamoId == null) completadas else completadas.filter { it.ramoId == filtroRamoId }

    val tabs = listOf("Pendientes", "Historial")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. DASHBOARD
            DashboardCard(
                totalTareas = totalTareas,
                tareasCompletadas = cantidadCompletadas
            )

            // 2. FILTROS
            if (ramos.isNotEmpty()) {
                RamoFilterRow(
                    ramos = ramos,
                    selectedRamoId = filtroRamoId,
                    onSelect = { filtroRamoId = it }
                )
            }

            // 3. PESTAÑAS
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        icon = {
                            Icon(
                                if (index == 0) Icons.Default.CheckCircle else Icons.Default.Refresh,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // 4. LISTA DE TAREAS
            when (tabIndex) {
                0 -> TareasList(
                    tareas = pendientesFiltradas,
                    esHistorial = false,
                    onAccion = { viewModel.completarTarea(it) },
                    // --- NUEVO: Callback para editar ---
                    onEdit = { tarea -> tareaParaEditar = tarea }
                )
                1 -> TareasList(
                    tareas = completadasFiltradas,
                    esHistorial = true,
                    onAccion = { viewModel.reactivarTarea(it) },
                    onDelete = { viewModel.borrarTareaDefinitiva(it) }
                    // En historial no permitimos editar, por eso no pasamos onEdit
                )
            }
        }

        // BOTÓN FLOTANTE (CREAR)
        if (tabIndex == 0) {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Tarea")
            }
        }

        // --- DIÁLOGO A: CREAR NUEVA TAREA ---
        if (mostrarDialogo) {
            val ramoPreseleccionado = ramos.find { it.id == filtroRamoId }

            AddTareaDialog(
                ramos = ramos,
                ramoInicial = ramoPreseleccionado,
                onDismiss = { mostrarDialogo = false },
                // Ignoramos ID y Estado porque es nueva
                onConfirm = { _, titulo, tipo, peso, fecha, ramoId, _ ->
                    viewModel.guardarNuevaTarea(titulo, tipo, peso, fecha, ramoId)
                    mostrarDialogo = false
                }
            )
        }

        // --- DIÁLOGO B: EDITAR TAREA EXISTENTE ---
        if (mostrarDialogoEdicion) {
            AddTareaDialog(
                ramos = ramos,
                tareaAEditar = tareaParaEditar, // Pasamos la tarea a editar
                onDismiss = { tareaParaEditar = null },
                onConfirm = { id, titulo, tipo, peso, fecha, ramoId, estado ->
                    if (id != null) {
                        viewModel.actualizarTarea(id, titulo, tipo, peso, fecha, ramoId, estado)
                    }
                    tareaParaEditar = null
                }
            )
        }
    }
}

// COMPONENTE REUTILIZABLE PARA LA LISTA
@Composable
fun TareasList(
    tareas: List<TareaEntity>,
    esHistorial: Boolean,
    onAccion: (TareaEntity) -> Unit,
    onDelete: ((TareaEntity) -> Unit)? = null,
    onEdit: ((TareaEntity) -> Unit)? = null // <--- NUEVO
) {
    if (tareas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (esHistorial) "No has completado tareas aún." else "¡Todo listo! A descansar.",
                color = MaterialTheme.colorScheme.secondary
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tareas) { tarea ->
                TareaItem(
                    tarea = tarea,
                    esHistorial = esHistorial,
                    onPrimaryClick = { onAccion(tarea) },
                    onDeleteClick = if (onDelete != null) { { onDelete(tarea) } } else null,
                    onEditClick = if (onEdit != null) { { onEdit(tarea) } } else null // <--- NUEVO
                )
            }
        }
    }
}

@Composable
fun TareaItem(
    tarea: TareaEntity,
    esHistorial: Boolean,
    onPrimaryClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null // <--- NUEVO
) {
    val colorPrioridad = if (esHistorial) Color.Gray else calcularColorPrioridad(tarea.fechaEntrega, tarea.peso)
    val alpha = if (esHistorial) 0.6f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (esHistorial) 1.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = alpha)
        )
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(12.dp)
                    .background(colorPrioridad)
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tarea.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (esHistorial) TextDecoration.LineThrough else null
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (!esHistorial) {
                        val dias = TimeUnit.MILLISECONDS.toDays(tarea.fechaEntrega - System.currentTimeMillis())
                        Text(
                            text = if(dias < 0) "Venció hace ${-dias} días" else "Faltan $dias días",
                            style = MaterialTheme.typography.bodySmall,
                            color = if(dias <= 2) Color.Red else Color.Gray
                        )
                    } else {
                        Text("Completada", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                Row {
                    // --- BOTÓN EDITAR (NUEVO) ---
                    // Solo mostramos el lápiz si no es historial y si nos pasaron la función
                    if (!esHistorial && onEditClick != null) {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Botón Principal (Check o Restore)
                    IconButton(onClick = onPrimaryClick) {
                        Icon(
                            imageVector = if (esHistorial) Icons.Default.Refresh else Icons.Default.Check,
                            contentDescription = if (esHistorial) "Reactivar" else "Completar",
                            tint = if (esHistorial) MaterialTheme.colorScheme.primary else Color(0xFF4CAF50)
                        )
                    }

                    // Botón Borrar (Solo historial)
                    if (onDeleteClick != null) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamoFilterRow(
    ramos: List<com.example.acz.data.local.entity.RamoEntity>,
    selectedRamoId: Long?,
    onSelect: (Long?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedRamoId == null,
                onClick = { onSelect(null) },
                label = { Text("Todos") },
                leadingIcon = if (selectedRamoId == null) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null
            )
        }

        items(ramos) { ramo ->
            FilterChip(
                selected = selectedRamoId == ramo.id,
                onClick = {
                    if (selectedRamoId == ramo.id) onSelect(null) else onSelect(ramo.id)
                },
                label = { Text(ramo.nombre) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}