package com.tuapp.zorro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Operaciones para Ramos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRamo(ramo: RamoEntity)

    @Query("SELECT * FROM ramos")
    fun obtenerTodosLosRamos(): Flow<List<RamoEntity>>

    // Operaciones para Tareas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarea(tarea: TareaEntity)

    @Query("SELECT * FROM tareas WHERE ramoId = :ramoId")
    fun obtenerTareasPorRamo(ramoId: Long): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas ORDER BY fechaEntrega ASC")
    fun obtenerTodasLasTareas(): Flow<List<TareaEntity>>
}