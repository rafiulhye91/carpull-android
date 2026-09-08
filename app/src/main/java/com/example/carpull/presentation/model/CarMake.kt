package com.example.carpull.presentation.model

import com.example.carpull.data.local.entity.CarMakeEntity
import kotlinx.serialization.Serializable

@Serializable
data class CarMake(
    val id: Long,
    val remoteId: Int?,
    val name: String,
    val notes: String? = null,
    val remoteName: String? = null,
    val isEdited: Boolean,
    val isDeleted: Boolean
)

fun CarMakeEntity.toCarMake() = CarMake(
    id = localId,
    remoteId = remoteId,
    name = name,
    notes = notes,
    remoteName = remoteName,
    isEdited = isEdited,
    isDeleted = isDeleted
)