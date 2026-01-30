package com.tuapp.zorro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "ramos")
data class RamoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val colorHex: String
)

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
    val fechaEntrega: Long,
    val peso: Double,
    val ramoId: Long,
    val tipo: String
)