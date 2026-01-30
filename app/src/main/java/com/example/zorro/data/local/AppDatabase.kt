package com.example.zorro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.ConstructedBy
import androidx.room.RoomDatabaseConstructor

// Archivo: AppDatabase.kt actualizado
@Database(
    entities = [RamoEntity::class, TareaEntity::class], // Agregamos TareaEntity
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ramoDao(): RamoDao
    abstract fun tareaDao(): TareaDao // Agregamos el DAO de tareas
}

// Este objeto es necesario para que KMP sepa cómo instanciar la DB en cada plataforma
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>