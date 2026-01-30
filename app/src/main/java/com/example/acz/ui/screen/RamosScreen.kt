package com.example.acz.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acz.data.local.entity.RamoEntity
import com.example.acz.ui.viewmodel.HomeViewModel

// Lista de colores predefinidos
val ColoresRamo = listOf(
    Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF5C6BC0),
    Color(0xFF42A5F5), Color(0xFF26A69A), Color(0xFF66BB6A), Color(0xFFFFA726),
    Color(0xFF8D6E63), Color(0xFF78909C)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamosScreen(
    onBack: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val ramos by viewModel.ramosDisponibles.collectAsState()
    var mostrarDialogoCrear by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (ramos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes asignaturas. ¡Agrega una!")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ramos) { ramo ->
                    RamoItem(
                        ramo = ramo,
                        onDelete = { viewModel.borrarRamo(ramo) }
                    )
                }
            }
        }

        // Botón flotante para agregar Ramo
        FloatingActionButton(
            onClick = { mostrarDialogoCrear = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) { Icon(Icons.Default.Add, contentDescription = "Agregar Ramo") }

        // DIÁLOGO CREAR RAMO
        if (mostrarDialogoCrear) {
            AddRamoDialog(
                onDismiss = { mostrarDialogoCrear = false },
                onConfirm = { nombre, colorHex, prof, mail ->
                    viewModel.guardarNuevoRamo(nombre, colorHex, prof, mail)
                    mostrarDialogoCrear = false
                }
            )
        }
    }
}

@Composable
fun RamoItem(ramo: RamoEntity, onDelete: () -> Unit) {
    val colorRamo = try { Color(android.graphics.Color.parseColor(ramo.colorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }

    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(colorRamo))
                Spacer(modifier = Modifier.width(16.dp))

                // DATOS DEL RAMO
                Column {
                    Text(text = ramo.nombre, style = MaterialTheme.typography.titleMedium)

                    if (!ramo.profesor.isNullOrBlank()) {
                        Text("Prof: ${ramo.profesor}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    if (!ramo.emailProfesor.isNullOrBlank()) {
                        Text("✉️ ${ramo.emailProfesor}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }

            // BOTÓN SOLO BORRAR (La gestión de notas ahora está en NotasScreen)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun AddRamoDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var profesor by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var colorSeleccionado by remember { mutableStateOf(ColoresRamo[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Asignatura") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = profesor, onValueChange = { profesor = it }, label = { Text("Profesor") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    ColoresRamo.take(5).forEach { color ->
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color)
                            .border(if (color == colorSeleccionado) 2.dp else 0.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            .clickable { colorSeleccionado = color })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val hexColor = String.format("#%06X", (0xFFFFFF and colorSeleccionado.toArgb()))
                onConfirm(nombre, hexColor, profesor, email)
            }) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}