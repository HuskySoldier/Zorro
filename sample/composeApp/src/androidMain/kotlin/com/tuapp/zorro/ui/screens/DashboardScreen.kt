package com.tuapp.zorro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tuapp.zorro.domain.model.Tarea
import com.tuapp.zorro.ui.components.AddTaskDialog
import com.tuapp.zorro.ui.components.RamoChip
import com.tuapp.zorro.ui.components.TareaCard
import com.tuapp.zorro.ui.viewmodel.AddSubjectViewModel
import com.tuapp.zorro.ui.viewmodel.AddTaskViewModel
import com.tuapp.zorro.ui.viewmodel.DashboardViewModel
import kotlinx.datetime.Clock

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel,
    addTaskViewModel: AddTaskViewModel,
    addSubjectViewModel: AddSubjectViewModel
) {
    // Estado reactivo de la base de datos
    val state by dashboardViewModel.uiState.collectAsState()

    // Estado local para la visibilidad del diálogo (Lógica de UI)
    var showAddTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ACZ - Astuto como Zorro") },
                backgroundColor = Color(0xFF2E7D32), // Verde Bosque
                contentColor = Color.White,
                elevation = 0.dp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                backgroundColor = Color(0xFF2E7D32)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Tarea", tint = Color.White)
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2E7D32))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // SECCIÓN: RAMOS
                Text(
                    text = "Tus Materias",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (state.ramos.isEmpty()) {
                    Text("No hay ramos aún. ¡Empieza a cazar!", style = MaterialTheme.typography.caption)
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.ramos) { ramo ->
                            RamoChip(ramo)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SECCIÓN: TAREAS PRIORIZADAS
                Text(
                    text = "Prioridades Estratégicas",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (state.tareasPrioritarias.isEmpty()) {
                    // CORRECCIÓN: .weight(1f) en lugar de fillWeight
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Todo despejado.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(state.tareasPrioritarias) { tarea ->
                            val prioridad = calcularPrioridadEnUI(tarea)
                            TareaCard(tarea = tarea, prioridad = prioridad)
                        }
                    }
                }
            }
        }

        // LÓGICA DEL DIÁLOGO PARA AGREGAR TAREA
        if (showAddTaskDialog) {
            AddTaskDialog(
                onDismiss = { showAddTaskDialog = false },
                onConfirm = { ramoId ->
                    addTaskViewModel.guardarTarea(ramoId)
                    showAddTaskDialog = false
                },
                viewModel = addTaskViewModel,
                ramos = state.ramos
            )
        }
    }
}

/**
 * Función auxiliar para mostrar la prioridad en tiempo real.
 * P = peso * (1 / (dias + 1))
 */
fun calcularPrioridadEnUI(tarea: Tarea): Double {
    val ahora = Clock.System.now().toEpochMilliseconds()
    val unDiaEnMs = 86400000L
    val diferenciaMs = tarea.fechaEntrega - ahora
    val diasRestantes = (diferenciaMs / unDiaEnMs).coerceAtLeast(0)

    return tarea.peso * (1.0 / (diasRestantes + 1))
}