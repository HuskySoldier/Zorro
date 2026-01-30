package com.example.zorro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ramos")
data class RamoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val colorHex: String,
    val semestreId: Long
)