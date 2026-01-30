package com.tuapp.zorro.sample.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tuapp.zorro.data.local.getDatabaseBuilder
import com.tuapp.zorro.data.repository.ACZRepositoryImpl
import com.tuapp.zorro.ui.screens.DashboardScreen
import com.tuapp.zorro.ui.viewmodel.DashboardViewModel
import com.tuapp.zorro.ui.viewmodel.AddTaskViewModel      // Nuevo Import
import com.tuapp.zorro.ui.viewmodel.AddSubjectViewModel  // Nuevo Import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializamos la base de datos
        val dbBuilder = getDatabaseBuilder(applicationContext)
        val db = dbBuilder.build()

        // 2. Repositorio único (Single Source of Truth)
        val repository = ACZRepositoryImpl(db.dao())

        // 3. Instanciamos todos los ViewModels necesarios para ACZ
        val dashboardVM = DashboardViewModel(repository)
        val addTaskVM = AddTaskViewModel(repository)
        val addSubjectVM = AddSubjectViewModel(repository)

        setContent {
            // Llamamos a la pantalla pasando los tres ViewModels
            DashboardScreen(
                dashboardViewModel = dashboardVM,
                addTaskViewModel = addTaskVM,
                addSubjectViewModel = addSubjectVM
            )
        }
    }
}