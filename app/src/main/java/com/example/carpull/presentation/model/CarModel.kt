package com.example.carpull.presentation.model

import com.example.carpull.data.local.entity.CarModelEntity

data class CarModel(
    val modelId: Int,
    val makeId: Int,
    val name: String
)

fun CarModelEntity.toCarModel() = CarModel(
    modelId = modelId,
    makeId = makeRemoteId,
    name = name
)