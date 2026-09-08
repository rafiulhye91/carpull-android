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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ICarMakeViewModel {
    fun onItemDelete(carMake: CarMake)
    fun onSortOrderChange(sortOrder: SortOrder)
    fun onQueryChange(query: String)
}

@HiltViewModel
class CarMakeViewModel @Inject constructor(private val repository: ICarPullRepository) :
    ICarMakeViewModel, ViewModel() {

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    private val _allCarMakes = MutableStateFlow<List<CarMake>>(emptyList())

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val carMakes: StateFlow<List<CarMake>> =
        combine(_allCarMakes, _query) { makes, query ->
            if (query.isBlank()) makes
            else makes.filter { it.name.contains(query.trim(), ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
                        _allCarMakes.value = makes
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

    override fun onQueryChange(query: String) {
        _query.value = query
    }

    override fun onItemDelete(carMake: CarMake) {
        viewModelScope.launch {
            repository.deleteCarMake(carMake)
        }
    }

}