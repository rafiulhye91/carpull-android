package com.example.carpull.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpull.data.Resource
import com.example.carpull.repositories.ICarPullRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ICarMakeViewModel {
}

@HiltViewModel
class CarMakeViewModel @Inject constructor(private val repository: ICarPullRepository) :
    ICarMakeViewModel, ViewModel() {

    init {
        getAllMakes()
    }

    private fun getAllMakes() {
        viewModelScope.launch {
            repository.getAllMakes().collect { resource ->
                when (resource) {
                    is Resource.Error -> { Log.d("rafi", "getAllMakes: ${resource.error}")}
                    is Resource.Loading -> {
                        Log.d("rafi", "getAllMakes: Loading")
                    }
                    is Resource.Success -> {
                        val makes = resource.data.orEmpty()
                        if (makes.isNullOrEmpty()) {
                            refreshAllMakes()
                            return@collect
                        }
                        Log.d("rafi", "total make size: ${makes.size}")
                    }
                }
            }

        }
    }

    private fun refreshAllMakes() {
        viewModelScope.launch {
            repository.refreshMakes().collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        Log.d("rafi", "refreshAllMakes: ${resource.error}")
                    }
                    is Resource.Loading -> {
                        Log.d("rafi", "refreshAllMakes: Loading")
                    }
                    is Resource.Success -> {
                        Log.d("rafi", "refreshAllMakes: ${resource.data}")
                    }
                }
            }
    }
}

}