package com.example.carpull.presentation.model

import com.example.carpull.data.local.entity.CarMakeEntity

data class CarMake(
    val id: Long,
    val name: String,
    val isEdited: Boolean,
)

fun CarMakeEntity.toCarMake() = CarMake(
    id = localId,
    name = name,
    isEdited = isEdited,
)