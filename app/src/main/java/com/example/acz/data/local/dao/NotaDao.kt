package com.example.acz.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.acz.data.local.entity.NotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    @Query("SELECT * FROM notas WHERE ramoId = :ramoId")
    fun getNotasPorRamo(ramoId: Long): Flow<List<NotaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNota(nota: NotaEntity)

    @Delete
    suspend fun deleteNota(nota: NotaEntity)

    @Update
    suspend fun updateNota(nota: NotaEntity)
}