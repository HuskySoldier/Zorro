package com.example.zorro.data.local

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas ORDER BY fechaEntrega ASC")
    fun getAllTareas(): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE ramoId = :ramoId")
    fun getTareasByRamo(ramoId: Long): Flow<List<TareaEntity>>

    @Insert
    suspend fun insertTarea(tarea: TareaEntity)

    @Update
    suspend fun updateTarea(tarea: TareaEntity)

    @Delete
    suspend fun deleteTarea(tarea: TareaEntity)
}