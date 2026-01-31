package com.example.acz.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "horario",
    foreignKeys = [
        ForeignKey(
            entity = RamoEntity::class,
            parentColumns = ["id"],
            childColumns = ["ramoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HorarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diaSemana: Int, // 1 = Lunes, 2 = Martes, etc.
    val horaInicio: String, // "08:30"
    val horaFin: String,    // "10:00"
    val sala: String?,      // "Aula 204"
    val ramoId: Long
)