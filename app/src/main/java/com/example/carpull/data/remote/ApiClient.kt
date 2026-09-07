package com.example.carpull.data.remote

import com.example.carpull.data.remote.model.AllMakesDTO
import retrofit2.Response
import retrofit2.http.GET

interface ApiServices {
    companion object {
        const val BASE_URL = "https://vpic.nhtsa.dot.gov/api/"
        const val TIMEOUT: Long = 5
    }

    @GET("vehicles/getallmakes?format=json")
    suspend fun getAllMakes(): Response<AllMakesDTO?>
}