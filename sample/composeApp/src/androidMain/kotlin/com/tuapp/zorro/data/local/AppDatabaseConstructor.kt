package com.tuapp.zorro.data.local

import androidx.room.RoomDatabaseConstructor

// Este es el "puente" que Room necesita en KMP
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>