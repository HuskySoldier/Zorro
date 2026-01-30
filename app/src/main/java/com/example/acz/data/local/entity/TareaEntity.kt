package com.example.acz.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = RamoEntity::class,
            parentColumns = ["id"],
            childColumns = ["ramoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String? = null,
    val tipo: String, // Cambié el Enum a String por simplicidad inicial
    val fechaEntrega: Long,
    val peso: Int,
    val estado: String = "PENDIENTE", // Cambié Enum a String por ahora
    val ramoId: Long
)