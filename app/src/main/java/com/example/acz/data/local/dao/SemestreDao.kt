package com.example.acz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.acz.data.local.entity.SemestreEntity // Importa tu entidad
import kotlinx.coroutines.flow.Flow

@Dao
interface SemestreDao {

    // Insertar un semestre. Si ya existe ID, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemestre(semestre: SemestreEntity): Long

    // Obtener todos los semestres para una lista
    @Query("SELECT * FROM semestres ORDER BY id DESC")
    fun getAllSemestres(): Flow<List<SemestreEntity>>
    // Nota: 'Flow' avisa a la UI automáticamente si algo cambia

    // Borrar un semestre (y por cascada, sus ramos y tareas)
    @Query("DELETE FROM semestres WHERE id = :id")
    suspend fun deleteSemestre(id: Long)
}