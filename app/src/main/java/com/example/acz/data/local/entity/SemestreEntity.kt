package com.example.acz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semestres")
data class SemestreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String, // Ej: "2026-1"
    val esActual: Boolean = false
)