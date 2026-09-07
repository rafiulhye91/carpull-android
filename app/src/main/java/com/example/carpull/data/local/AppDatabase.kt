package com.example.carpull.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.carpull.data.local.entity.CarMakeEntity
import com.example.carpull.data.local.entity.CarModelEntity

@Database(
    entities = [CarMakeEntity::class, CarModelEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): AppDao

    companion object {
        const val DATABASE_NAME = "car_pull_db"
    }
}