package com.tuapp.zorro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.ConstructedBy

@Database(entities = [RamoEntity::class, TareaEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}

