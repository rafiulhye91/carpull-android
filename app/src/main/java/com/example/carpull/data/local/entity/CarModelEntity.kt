package com.example.carpull.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.carpull.data.remote.model.ModelInfoDTO

@Entity(
    tableName = "models",
    indices = [
        Index(value = ["makeLocalId"]),
        Index(value = ["remoteId"], unique = true),
    ]
)
data class CarModelEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val remoteId: Int? = null,
    val makeLocalId: Long,
    val name: String,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val fetchedAt: Long = System.currentTimeMillis()
)

fun ModelInfoDTO.toEntity(makeLocalId: Long) = CarModelEntity(
    remoteId = modelId,
    makeLocalId = makeLocalId,
    name = modelName
)
