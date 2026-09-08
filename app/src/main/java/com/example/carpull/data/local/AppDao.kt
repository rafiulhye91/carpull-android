package com.example.carpull.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("SELECT * FROM makes WHERE localId = :localId")
    suspend fun getMakeById(localId: Long): CarMakeEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMake(make: CarMakeEntity): Long

    @Query(
        """
        UPDATE makes SET
            remoteName = COALESCE(remoteName, name),
            name = :name,
            notes = :notes,
            isEdited = 1,
            updatedAt = :updatedAt
        WHERE localId = :localId
        """
    )
    suspend fun updateMake(localId: Long, name: String, notes: String?, updatedAt: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllModels(models: List<CarModelEntity>): List<Long>

    @Query(
        """
        SELECT * FROM models
        WHERE makeLocalId = :makeLocalId AND isDeleted = 0
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun getModelsForMake(makeLocalId: Long): Flow<List<CarModelEntity>>

    @Transaction
    suspend fun saveMakeWithNewModels(
        localId: Long?,
        name: String,
        notes: String?,
        newModelNames: List<String>,
        now: Long,
    ): Long {
        val makeId = if (localId == null) {
            insertMake(CarMakeEntity(name = name, notes = notes, updatedAt = now))
        } else {
            if (updateMake(localId, name, notes, now) == 0) return -1L
            localId
        }
        if (newModelNames.isNotEmpty()) {
            insertAllModels(
                newModelNames.map {
                    CarModelEntity(
                        makeLocalId = makeId,
                        name = it,
                        updatedAt = now,
                        fetchedAt = now,
                    )
                }
            )
        }
        return makeId
    }
}