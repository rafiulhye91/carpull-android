package com.example.carpull.presentation.model

enum class CarMakeFormMode { Add, Edit }

data class CarMakeForm(
    val name: String = "",
    val notes: String = "",
    val originalName: String? = null,
    val existingModels: List<CarModel> = emptyList(),
    val newModelNames: List<String> = emptyList(),
    val modelDraft: String = "",
)