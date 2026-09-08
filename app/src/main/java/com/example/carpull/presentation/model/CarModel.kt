package com.example.carpull.presentation.model

import com.example.carpull.data.local.entity.CarModelEntity

data class CarModel(
    val id: Long,
    val remoteId: Int?,
    val makeLocalId: Long,
    val name: String
)

fun CarModelEntity.toCarModel() = CarModel(
    id = localId,
    remoteId = remoteId,
    makeLocalId = makeLocalId,
    name = name
)