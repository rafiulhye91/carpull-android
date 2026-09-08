package com.example.carpull.data.remote.model

import com.google.gson.annotations.SerializedName

data class AllModelsDTO(
    @SerializedName(value = "Count") val count: Int,
    @SerializedName(value = "Message") val message: String,
    @SerializedName(value = "Results") val models: List<ModelInfoDTO>
)