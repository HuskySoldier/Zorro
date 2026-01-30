package com.example.zorro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface RamoDao {
    @Query("SELECT * FROM ramos")
    fun getAllRamos(): Flow<List<RamoEntity>>

    @Insert
    suspend fun insertRamo(ramo: RamoEntity)

    @Delete
    suspend fun deleteRamo(ramo: RamoEntity)

    @Query("SELECT * FROM ramos WHERE id = :id")
    suspend fun getRamoById(id: Long): RamoEntity?
}