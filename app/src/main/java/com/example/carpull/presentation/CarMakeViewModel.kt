package com.example.carpull.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpull.data.Resource
import com.example.carpull.presentation.model.CarMake
import com.example.carpull.presentation.model.SortOrder
import com.example.carpull.repositories.ICarPullRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ICarMakeViewModel {
    fun onItemDelete(carMake: CarMake)
    fun onSortOrderChange(sortOrder: SortOrder)
}

@HiltViewModel
class CarMakeViewModel @Inject constructor(private val repository: ICarPullRepository) :
    ICarMakeViewModel, ViewModel() {

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    private val _carMakes = MutableStateFlow<List<CarMake>>(emptyList())
    val carMakes = _carMakes.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NameAsc)
    val sortOrder = _sortOrder.asStateFlow()

    private var makesJob: Job? = null
    private var hasRequestedRefresh = false

    init {
        getAllMakes()
    }

    private fun getAllMakes() {
        makesJob?.cancel()
        makesJob = viewModelScope.launch {
            repository.getAllMakesFromLocal(_sortOrder.value).collect { resource ->
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
                        val makes = resource.data.orEmpty()
                        if (makes.isEmpty() && !hasRequestedRefresh) {
                            hasRequestedRefresh = true
                            refreshAllMakes()
                            return@collect
                        }
                        _carMakes.value = makes
                    }
                }
            }

        }
    }

    private fun refreshAllMakes() {
        viewModelScope.launch {
            repository.getAllMakesFromRemote().collect { resource ->
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

    override fun onSortOrderChange(sortOrder: SortOrder) {
        if (_sortOrder.value == sortOrder) return
        _sortOrder.value = sortOrder
        getAllMakes()
    }

    override fun onItemDelete(carMake: CarMake) {
        viewModelScope.launch {
            repository.deleteCarMake(carMake)
        }
    }

}