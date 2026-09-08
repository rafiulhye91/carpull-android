package com.example.carpull.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.carpull.data.local.entity.CarMakeEntity
import com.example.carpull.data.local.entity.CarModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMakes(makes: List<CarMakeEntity>): List<Long>

    @Query("SELECT * FROM makes WHERE isDeleted = 0 ORDER BY name COLLATE NOCASE ASC")
    fun getAllMakes(): Flow<List<CarMakeEntity>>

    @Query("UPDATE makes SET isDeleted = 1, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun softDeleteMake(localId: Long, updatedAt: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllModels(models: List<CarModelEntity>): List<Long>

    @Query("SELECT * FROM models WHERE makeRemoteId = :makeRemoteId ORDER BY name COLLATE NOCASE ASC")
    fun getModelsForMake(makeRemoteId: Int): Flow<List<CarModelEntity>>
}