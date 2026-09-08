package com.example.carpull.data.remote.model

import com.google.gson.annotations.SerializedName

data class ModelInfoDTO(
    @SerializedName(value = "Make_ID") val makeId: Int,
    @SerializedName(value = "Make_Name") val makeName: String,
    @SerializedName(value = "Model_ID") val modelId: Int,
    @SerializedName(value = "Model_Name") val modelName: String
)