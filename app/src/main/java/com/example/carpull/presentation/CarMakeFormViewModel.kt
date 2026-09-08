package com.example.carpull.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.carpull.data.Resource
import com.example.carpull.presentation.model.CarMakeForm
import com.example.carpull.presentation.model.CarMakeFormMode
import com.example.carpull.repositories.ICarPullRepository
import com.example.carpull.view.navigation.CarMakeFormRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ICarMakeFormViewModel {
    fun onNameChange(value: String)
    fun onNotesChange(value: String)
    fun onModelDraftChange(value: String)
    fun onAddModel()
    fun onRemoveNewModel(index: Int)
    fun onSave()
    fun onSavedHandled()
}

@HiltViewModel
class CarMakeFormViewModel @Inject constructor(
    private val repository: ICarPullRepository,
    savedStateHandle: SavedStateHandle,
) : ICarMakeFormViewModel, ViewModel() {

    private val localId: Long? = savedStateHandle.toRoute<CarMakeFormRoute>().localId

    val mode: CarMakeFormMode =
        if (localId == null) CarMakeFormMode.Add else CarMakeFormMode.Edit

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    private val _form = MutableStateFlow(CarMakeForm())
    val form = _form.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    init {
        localId?.let {
            loadCarMake(it)
            observeModels(it)
        }
    }

    private fun loadCarMake(localId: Long) {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = repository.getCarMake(localId)) {
                is Resource.Error -> {
                    _loadingState.value = false
                    _errorState.value = result.error
                }

                else -> {
                    _loadingState.value = false
                    _errorState.value = null
                    result.data?.let { carMake ->
                        _form.value = _form.value.copy(
                            name = carMake.name,
                            notes = carMake.notes.orEmpty(),
                            originalName = carMake.remoteName,
                        )
                    }
                }
            }
        }
    }

    private fun observeModels(localId: Long) {
        viewModelScope.launch {
            repository.getAllModelsFromLocal(localId).collect { resource ->
                when (resource) {
                    is Resource.Error -> _errorState.value = resource.error
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        _form.value = _form.value.copy(
                            existingModels = resource.data.orEmpty()
                        )
                    }
                }
            }
        }
    }

    override fun onNameChange(value: String) {
        _form.value = _form.value.copy(name = value)
        if (value.isNotBlank()) _errorState.value = null
    }

    override fun onNotesChange(value: String) {
        _form.value = _form.value.copy(notes = value)
    }

    override fun onModelDraftChange(value: String) {
        _form.value = _form.value.copy(modelDraft = value)
    }

    override fun onAddModel() {
        val draft = _form.value.modelDraft.trim()
        if (draft.isEmpty()) return
        _form.value = _form.value.copy(
            newModelNames = _form.value.newModelNames + draft,
            modelDraft = "",
        )
    }

    override fun onRemoveNewModel(index: Int) {
        val current = _form.value.newModelNames
        if (index !in current.indices) return
        _form.value = _form.value.copy(
            newModelNames = current.filterIndexed { i, _ -> i != index }
        )
    }

    override fun onSave() {
        val form = _form.value
        if (form.name.isBlank()) {
            _form.value = form.copy(hasAttemptedSave = true)
            _errorState.value = "Name can't be empty"
            return
        }
        viewModelScope.launch {
            _loadingState.value = true
            val result = repository.saveCarMake(
                localId = localId,
                name = form.name.trim(),
                notes = form.notes.trim().ifEmpty { null },
                newModelNames = form.newModelNames,
            )
            _loadingState.value = false
            when (result) {
                is Resource.Error -> _errorState.value = result.error
                else -> {
                    _errorState.value = null
                    _isSaved.value = true
                }
            }
        }
    }

    override fun onSavedHandled() {
        _isSaved.value = false
    }
}
