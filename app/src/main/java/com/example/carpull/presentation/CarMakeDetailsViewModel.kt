package com.example.carpull.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.carpull.data.Resource
import com.example.carpull.presentation.model.CarMake
import com.example.carpull.presentation.model.CarModel
import com.example.carpull.repositories.ICarPullRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ICarMakeDetailsViewModel {
}

@HiltViewModel
class CarMakeDetailsViewModel @Inject constructor(
    private val repository: ICarPullRepository,
    savedStateHandle: SavedStateHandle,
) : ICarMakeDetailsViewModel, ViewModel() {

    val carMake: CarMake = savedStateHandle.toRoute<CarMake>()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    private val _carModels = MutableStateFlow<List<CarModel>>(emptyList())
    val carModels = _carModels.asStateFlow()

    private var hasRequestedRefresh = false

    init {
        getAllModels()
    }

    private fun getAllModels() {
        viewModelScope.launch {
            repository.getAllModelsFromLocal(carMake.id).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _loadingState.value = false
                        _errorState.value = resource.error
                    }

                    is Resource.Loading -> {
                        _loadingState.value = true
                        _errorState.value = null
                    }

                    is Resource.Success -> {
                        _loadingState.value = false
                        _errorState.value = null
                        val models = resource.data.orEmpty()
                        val remoteId = carMake.remoteId
                        if (models.isEmpty() && remoteId != null && !hasRequestedRefresh) {
                            hasRequestedRefresh = true
                            refreshAllModels(remoteId)
                            return@collect
                        }
                        _carModels.value = models
                    }
                }
            }
        }
    }

    private fun refreshAllModels(remoteId: Int) {
        viewModelScope.launch {
            repository.getAllModelsFromRemote(carMake.id, remoteId).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _loadingState.value = false
                        _errorState.value = resource.error
                    }

                    is Resource.Loading -> {
                        _loadingState.value = true
                        _errorState.value = null
                    }

                    is Resource.Success -> {
                        _loadingState.value = false
                        _errorState.value = null
                    }
                }
            }
        }
    }
}
