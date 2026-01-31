package com.example.acz

import android.app.Application
import com.example.acz.data.local.AppDatabase
import com.example.acz.data.repositories.AppRepository

class AczApplication : Application() {
    // Inicializamos la base de datos de forma perezosa
    val database by lazy { AppDatabase.getDatabase(this) }

    // Inicializamos el repositorio
    val repository by lazy {
        AppRepository(
            database.semestreDao(),
            database.ramoDao(),
            database.tareaDao(),
            database.NotaDao(),
            database.HorarioDao()
        )
    }
}