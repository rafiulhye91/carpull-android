package com.example.carpull.repositories

import com.example.carpull.data.Resource
import com.example.carpull.data.Resource.*
import com.example.carpull.data.local.AppDao
import com.example.carpull.data.local.entity.CarMakeEntity
import com.example.carpull.data.local.entity.CarModelEntity
import com.example.carpull.data.local.entity.toEntity
import com.example.carpull.data.remote.ApiServices
import com.example.carpull.presentation.model.CarMake
import com.example.carpull.presentation.model.CarModel
import com.example.carpull.presentation.model.SortOrder
import com.example.carpull.presentation.model.toCarMake
import com.example.carpull.presentation.model.toCarModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

interface ICarPullRepository {
    suspend fun getAllMakesFromLocal(sortOrder: SortOrder): Flow<Resource<List<CarMake>>>
    suspend fun getAllMakesFromRemote(): Flow<Resource<Int>>
    suspend fun deleteCarMake(carMake: CarMake): Resource<Int>
    suspend fun getCarMake(localId: Long): Resource<CarMake>
    suspend fun saveCarMake(
        localId: Long?,
        name: String,
        notes: String?,
        newModelNames: List<String>,
    ): Resource<Long>

    suspend fun getAllModelsFromLocal(makeLocalId: Long): Flow<Resource<List<CarModel>>>
    suspend fun getAllModelsFromRemote(makeLocalId: Long, makeRemoteId: Int): Flow<Resource<Int>>
}

class CarPullRepository @Inject constructor(
    private val api: ApiServices,
    private val dao: AppDao
) : ICarPullRepository {

    override suspend fun getAllMakesFromLocal(sortOrder: SortOrder): Flow<Resource<List<CarMake>>> {
        val source = when (sortOrder) {
            SortOrder.NameAsc -> dao.getAllMakesByNameAsc()
            SortOrder.NameDesc -> dao.getAllMakesByNameDesc()
            SortOrder.LastEdited -> dao.getAllMakesByLastEdited()
        }
        return source
            .map<List<CarMakeEntity>, Resource<List<CarMake>>> { it -> Success(it.map { it.toCarMake() }) }
            .catch { e ->
                emit(Error(error = "Couldn't read local data: ${e.message}"))
            }
    }

    override suspend fun getAllMakesFromRemote(): Flow<Resource<Int>> = flow {
        emit(Loading())

        val response = api.getAllMakes()
        if (!response.isSuccessful) {
            emit(Error(error = "Server error (HTTP ${response.code()})"))
            return@flow
        }

        val makes = response.body()?.makeInfoList.orEmpty()
        if (makes.isEmpty()) {
            emit(Error(error = "API returned no makes"))
            return@flow
        }
        val rowIds = dao.insertAllMakes(makes.map { it.toEntity() })
        emit(Success(rowIds.count { it != -1L }))
    }.catch { e ->
        emit(Error(error = "Failed to refresh: ${e.message}"))
    }

    override suspend fun deleteCarMake(carMake: CarMake): Resource<Int> {
        return try {
            val updated = dao.softDeleteMake(
                localId = carMake.id,
                updatedAt = System.currentTimeMillis(),
            )
            if (updated == 0) {
                Error(error = "Couldn't find that make")
            } else {
                Success(updated)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Error(error = "Failed to delete: ${e.message}")
        }
    }

    override suspend fun getCarMake(localId: Long): Resource<CarMake> {
        return try {
            val make = dao.getMakeById(localId)
            if (make == null) {
                Error(error = "Couldn't find that make")
            } else {
                Success(make.toCarMake())
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Error(error = "Failed to load: ${e.message}")
        }
    }

    override suspend fun saveCarMake(
        localId: Long?,
        name: String,
        notes: String?,
        newModelNames: List<String>,
    ): Resource<Long> {
        return try {
            val makeId = dao.saveMakeWithNewModels(
                localId = localId,
                name = name,
                notes = notes,
                newModelNames = newModelNames,
                now = System.currentTimeMillis(),
            )
            if (makeId == -1L) {
                Error(error = "Couldn't find that make")
            } else {
                Success(makeId)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Error(error = "Failed to save: ${e.message}")
        }
    }

    override suspend fun getAllModelsFromLocal(makeLocalId: Long): Flow<Resource<List<CarModel>>> {
        return dao.getModelsForMake(makeLocalId)
            .map<List<CarModelEntity>, Resource<List<CarModel>>> { it -> Success(it.map { it.toCarModel() }) }
            .catch { e ->
                emit(Error(error = "Couldn't read local data: ${e.message}"))
            }
    }

    override suspend fun getAllModelsFromRemote(
        makeLocalId: Long,
        makeRemoteId: Int,
    ): Flow<Resource<Int>> = flow {
        emit(Loading())
        val response = api.getModelsForMake(makeRemoteId)
        if (!response.isSuccessful) {
            emit(Error(error = "Server error (HTTP ${response.code()})"))
            return@flow
        }
        val models = response.body()?.models.orEmpty()
        if (models.isEmpty()) {
            emit(Success(0))
            return@flow
        }
        val rowIds = dao.insertAllModels(models.map { it.toEntity(makeLocalId) })
        emit(Success(rowIds.size))
    }.catch { e ->
        emit(Error(error = "Failed to refresh: ${e.message}"))
    }

}