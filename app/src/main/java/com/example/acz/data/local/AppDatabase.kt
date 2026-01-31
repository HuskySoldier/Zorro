package com.example.acz.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.acz.data.local.dao.HorarioDao
import com.example.acz.data.local.dao.NotaDao
import com.example.acz.data.local.dao.RamoDao
import com.example.acz.data.local.dao.SemestreDao
import com.example.acz.data.local.dao.TareaDao
import com.example.acz.data.local.entity.HorarioEntity
import com.example.acz.data.local.entity.NotaEntity
import com.example.acz.data.local.entity.RamoEntity
import com.example.acz.data.local.entity.SemestreEntity
import com.example.acz.data.local.entity.TareaEntity

@Database(
    entities = [SemestreEntity::class, RamoEntity::class, TareaEntity::class, NotaEntity::class, HorarioEntity::class], // Aquí registras tus tablas
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun semestreDao(): SemestreDao
    abstract fun ramoDao(): RamoDao
    abstract fun tareaDao(): TareaDao

    abstract fun HorarioDao(): HorarioDao

    abstract fun NotaDao(): NotaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // <--- AGREGA ESTO PARA EVITAR CRASHES EN DESARROLLO
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}