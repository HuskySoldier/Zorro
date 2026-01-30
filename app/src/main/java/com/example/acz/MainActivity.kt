package com.example.acz

import android.Manifest // <--- NUEVO
import android.os.Build // <--- NUEVO
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy // <--- NUEVO
import androidx.work.PeriodicWorkRequestBuilder // <--- NUEVO
import androidx.work.WorkManager // <--- NUEVO
import com.example.acz.ui.screen.HomeScreen
import com.example.acz.ui.screen.RamosScreen
import com.example.acz.ui.theme.ACZTheme
import com.example.acz.worker.RecordatorioWorker // <--- NUEVO (Asegúrate de haber creado el archivo del paso anterior)
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit // <--- NUEVO

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. PEDIR PERMISO DE NOTIFICACIONES (Solo Android 13+) <--- NUEVO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // 2. PROGRAMAR EL WORKER DE SEGUNDO PLANO <--- NUEVO
        programarRecordatorios()

        setContent {
            ACZTheme {
                MainAppStructure()
            }
        }
    }

    // FUNCIÓN AUXILIAR PARA PROGRAMAR LAS NOTIFICACIONES <--- NUEVO
    private fun programarRecordatorios() {
        // Creamos una petición para que se repita cada 12 horas
        val workRequest = PeriodicWorkRequestBuilder<RecordatorioWorker>(12, TimeUnit.HOURS)
            .setInitialDelay(10, TimeUnit.SECONDS) // Espera 10 seg antes de la primera vez (para probar)
            .build()

        // Lo encolamos (KEEP significa que si ya existe, no lo duplique)
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

                // Opción 1: Inicio
                NavigationDrawerItem(
                    label = { Text("Mis Tareas") },
                    selected = false, // Podrías mejorar esto detectando la ruta actual
                    icon = { Icon(Icons.Default.Home, null) },
                    onClick = {
                        scope.launch {
                            navController.navigate("home")
                            drawerState.close()
                        }
                    }
                )

                // Opción 2: Ramos
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
            }
        }
    ) {
        // Contenido Principal con la Barra Superior Compartida
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Organizador") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
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
            // El NavHost ahora vive DENTRO del Scaffold global
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") {
                    // Ya no necesitamos pasar navegación manual ni TopBar interna
                    HomeScreen()
                }
                composable("ramos") {
                    RamosScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}