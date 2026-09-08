package com.example.carpull.presentation.model

import com.example.carpull.data.local.entity.CarMakeEntity
import kotlinx.serialization.Serializable

@Serializable
data class CarMake(
    val id: Long,
    val remoteId: Int?,
    val name: String,
    val isEdited: Boolean,
    val isDeleted: Boolean
)

fun CarMakeEntity.toCarMake() = CarMake(
    id = localId,
    remoteId = remoteId,
    name = name,
    isEdited = isEdited,
    isDeleted = isDeleted
)