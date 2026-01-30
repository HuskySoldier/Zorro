package com.example.acz.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ramos",
    foreignKeys = [
        ForeignKey(
            entity = SemestreEntity::class,
            parentColumns = ["id"],
            childColumns = ["semestreId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RamoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val colorHex: String,
    val semestreId: Long,
    val progreso: Float = 0f,
    // --- NUEVOS CAMPOS ---
    val profesor: String? = null,
    val emailProfesor: String? = null
)