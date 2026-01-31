package com.example.acz

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star // Icono para Notas
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.acz.ui.screen.HomeScreen
import com.example.acz.ui.screen.HorarioScreen
import com.example.acz.ui.screen.NotasScreen // <--- IMPORTANTE: Importar la nueva pantalla
import com.example.acz.ui.screen.RamosScreen
import com.example.acz.ui.theme.ACZTheme
import com.example.acz.worker.RecordatorioWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. PEDIR PERMISO DE NOTIFICACIONES (Solo Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // 2. PROGRAMAR EL WORKER DE SEGUNDO PLANO
        programarRecordatorios()

        setContent {
            ACZTheme {
                MainAppStructure()
            }
        }
    }

    private fun programarRecordatorios() {
        val workRequest = PeriodicWorkRequestBuilder<RecordatorioWorker>(12, TimeUnit.HOURS)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "work_recordatorio_acad",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppStructure() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Envoltura del Menú Lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Menú Académico", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()

                // OPCIÓN 1: INICIO (TAREAS)
                NavigationDrawerItem(
                    label = { Text("Mis Tareas") },
                    selected = false,
                    icon = { Icon(Icons.Default.Home, null) },
                    onClick = {
                        scope.launch {
                            navController.navigate("home")
                            drawerState.close()
                        }
                    }
                )

                // OPCIÓN 2: RAMOS (GESTIÓN)
                NavigationDrawerItem(
                    label = { Text("Mis Ramos") },
                    selected = false,
                    icon = { Icon(Icons.Default.List, null) },
                    onClick = {
                        scope.launch {
                            navController.navigate("ramos")
                            drawerState.close()
                        }
                    }
                )

                // OPCIÓN 3: NOTAS (NUEVO)
                NavigationDrawerItem(
                    label = { Text("Mis Notas") },
                    selected = false,
                    icon = { Icon(Icons.Default.Star, null) }, // Usamos una Estrella para las notas
                    onClick = {
                        scope.launch {
                            // Navegamos a la ruta "notas"
                            navController.navigate("notas")
                            drawerState.close()
                        }
                    }
                )

                // OPCIÓN 4: HORARIO (NUEVO)
                NavigationDrawerItem(
                    label = { Text("Mi Horario") },
                    selected = false,
                    icon = { Icon(Icons.Default.DateRange, null) },
                    onClick = {
                        scope.launch {
                            navController.navigate("horario")
                            drawerState.close()
                        }
                    }
                )


            }
        }
    ) {
        // Contenido Principal
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Organizador") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply { if (isClosed) open() else close() }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { paddingValues ->

            // EL MAPA DE NAVEGACIÓN
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                // Ruta Home
                composable("home") {
                    HomeScreen()
                }

                // Ruta Ramos
                composable("ramos") {
                    RamosScreen(onBack = { navController.popBackStack() })
                }

                // Ruta Notas (AQUÍ ESTABA FALTANDO CONECTARLO)
                composable("notas") {
                    NotasScreen()
                }

                composable("horario") {
                    HorarioScreen()
                }
            }
        }
    }
}