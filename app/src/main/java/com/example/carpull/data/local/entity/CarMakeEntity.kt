package com.example.carpull.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.carpull.data.remote.model.MakeInfoDTO

@Entity(
    tableName = "makes",
    indices = [Index(value = ["remoteId"], unique = true)]
)
data class CarMakeEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val remoteId: Int? = null,
    val name: String,
    val remoteName: String? = null,
    val notes: String? = null,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

fun MakeInfoDTO.toEntity() = CarMakeEntity(
    remoteId = makeId,
    name = makeName
)