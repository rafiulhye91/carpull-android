package com.example.carpull.repositories

import com.example.carpull.data.Resource
import com.example.carpull.data.local.AppDao
import com.example.carpull.data.local.entity.CarMakeEntity
import com.example.carpull.data.local.entity.toEntity
import com.example.carpull.data.remote.ApiServices
import com.example.carpull.presentation.model.CarMake
import com.example.carpull.presentation.model.toCarMake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ICarPullRepository {
    suspend fun getAllMakes(): Flow<Resource<List<CarMake>>>
    suspend fun refreshMakes(): Flow<Resource<Int>>
}

class CarPullRepository @Inject constructor(
    private val api: ApiServices,
    private val dao: AppDao
) : ICarPullRepository {

    override suspend fun getAllMakes(): Flow<Resource<List<CarMake>>> {
        return dao.getAllMakes()
            .map<List<CarMakeEntity>, Resource<List<CarMake>>> { it -> Resource.Success(it.map { it.toCarMake() }) }
            .catch { e ->
                emit(Resource.Error(error = "Couldn't read local data: ${e.message}"))
            }
    }

    override suspend fun refreshMakes(): Flow<Resource<Int>> = flow {
        emit(Resource.Loading())

        val response = api.getAllMakes()
        if (!response.isSuccessful) {
            emit(Resource.Error(error = "Server error (HTTP ${response.code()})"))
            return@flow
        }

        val makes = response.body()?.makeInfoList.orEmpty()
        if (makes.isEmpty()) {
            emit(Resource.Error(error = "API returned no makes"))
            return@flow
        }
        val rowIds = dao.insertAllMakes(makes.map { it.toEntity() })
        emit(Resource.Success(rowIds.count { it != -1L }))
    }.catch { e ->
        emit(Resource.Error(error = "Failed to refresh: ${e.message}"))
    }

}