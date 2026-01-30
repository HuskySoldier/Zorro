package com.example.zorro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = RamoEntity::class,
            parentColumns = ["id"],
            childColumns = ["ramoId"],
            onDelete = ForeignKey.CASCADE // Si eliminas el ramo, se van sus tareas
        )
    ],
    indices = [Index(value = ["ramoId"])] // Optimiza las búsquedas por ramo
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val tipo: TipoTarea,       // Room Multiplatform puede manejar Enums automáticamente
    val fechaEntrega: Long,    // Timestamp (milisegundos)
    val peso: Double,          // Ej: 0.30 para un 30%
    val estado: EstadoTarea,
    val ramoId: Long           // La conexión con la tabla de Ramos
)