package com.example.acz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.acz.data.local.entity.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarea(tarea: TareaEntity)

    @Update
    suspend fun updateTarea(tarea: TareaEntity)

    @Query("DELETE FROM tareas WHERE id = :id")
    suspend fun deleteTarea(id: Long)

    // Para el Dashboard: Tareas pendientes ordenadas por fecha (lo más urgente primero)
    @Query("SELECT * FROM tareas WHERE estado = 'PENDIENTE' ORDER BY fechaEntrega ASC")
    fun getTareasPendientes(): Flow<List<TareaEntity>>

    // Para ver las tareas de un ramo específico
    @Query("SELECT * FROM tareas WHERE ramoId = :ramoId ORDER BY fechaEntrega ASC")
    fun getTareasByRamo(ramoId: Long): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE fechaEntrega BETWEEN :hoy AND :manana AND estado = 'PENDIENTE'")
    suspend fun getTareasProximas(hoy: Long, manana: Long): List<TareaEntity>

    @Query("SELECT * FROM tareas WHERE estado = 'COMPLETADA' ORDER BY fechaEntrega DESC")
    fun getTareasCompletadas(): Flow<List<TareaEntity>>
}