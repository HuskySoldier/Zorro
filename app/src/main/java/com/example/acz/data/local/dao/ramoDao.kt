package com.example.acz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.acz.data.local.entity.RamoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RamoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRamo(ramo: RamoEntity): Long

    // Obtener ramos de un semestre específico
    @Query("SELECT * FROM ramos WHERE semestreId = :semestreId")
    fun getRamosBySemestre(semestreId: Long): Flow<List<RamoEntity>>

    @Query("DELETE FROM ramos WHERE id = :id")
    suspend fun deleteRamo(id: Long)

    @Query("SELECT * FROM ramos")
    fun getAllRamos(): Flow<List<RamoEntity>>
}