package com.example.carpull.data.remote.model

import com.google.gson.annotations.SerializedName

data class AllMakesDTO(
    @SerializedName("Count") val count: Int = 0,
    @SerializedName("Message") val message: String? = null,
    @SerializedName("Results") val makeInfoList: List<MakeInfoDTO>? = null,
    @SerializedName("SearchCriteria") val searchCriteria: String? = null
)