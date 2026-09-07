package com.example.carpull.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.carpull.data.local.entity.CarMakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMakes(makes: List<CarMakeEntity>): List<Long>

    @Query("SELECT * FROM makes WHERE isDeleted = 0 ORDER BY name COLLATE NOCASE ASC")
    fun getAllMakes(): Flow<List<CarMakeEntity>>
}