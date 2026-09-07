package com.example.carpull.data.remote.model

import com.google.gson.annotations.SerializedName

data class MakeInfoDTO(
    @SerializedName("Make_ID") val makeId: Int = 0,
    @SerializedName("Make_Name") val makeName: String = ""
)