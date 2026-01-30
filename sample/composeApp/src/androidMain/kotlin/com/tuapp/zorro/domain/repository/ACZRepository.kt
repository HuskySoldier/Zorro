package com.tuapp.zorro.domain.repository

import com.tuapp.zorro.domain.model.Ramo
import com.tuapp.zorro.domain.model.Tarea
import kotlinx.coroutines.flow.Flow

interface ACZRepository {
    suspend fun guardarRamo(ramo: Ramo)
    fun obtenerTodosLosRamos(): Flow<List<Ramo>>
    // Tareas
    suspend fun guardarTarea(tarea: Tarea)
    fun obtenerTareasPriorizadas(): Flow<List<Tarea>>
}
