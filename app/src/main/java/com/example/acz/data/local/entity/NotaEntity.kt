package com.example.acz.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notas",
    foreignKeys = [
        ForeignKey(
            entity = RamoEntity::class,
            parentColumns = ["id"],
            childColumns = ["ramoId"],
            onDelete = ForeignKey.CASCADE // Si borras el ramo, se borran sus notas
        )
    ]
)
data class NotaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,     // Ej: "Solemne 1"
    val valor: Double,      // Ej: 5.5
    val porcentaje: Int,    // Ej: 30 (%)
    val ramoId: Long
)