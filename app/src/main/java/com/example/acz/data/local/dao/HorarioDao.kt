package com.example.acz.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.acz.data.local.entity.HorarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Query("SELECT * FROM horario ORDER BY horaInicio ASC")
    fun getAllHorarios(): Flow<List<HorarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHorario(horario: HorarioEntity)

    @Delete
    suspend fun deleteHorario(horario: HorarioEntity)
}