package com.example.carpull.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "models",
    indices = [
        Index(value = ["makeRemoteId"]),
        Index(value = ["modelId"], unique = true)
    ]
)
data class CarModelEntity(
    @PrimaryKey val modelId: Int,
    val makeRemoteId: Int,
    val name: String,
    val fetchedAt: Long = System.currentTimeMillis()
)