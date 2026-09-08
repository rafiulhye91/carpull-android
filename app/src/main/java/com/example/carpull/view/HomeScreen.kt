package com.example.carpull.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.carpull.presentation.CarMakeViewModel
import com.example.carpull.presentation.model.CarMake
import com.example.carpull.view.components.CarMakeList
import com.example.carpull.view.components.ErrorDialog
import com.example.carpull.view.components.LoadingDialog

@Composable
fun HomeScreen(
    onMakeClick: (CarMake) -> Unit,
    onEditClick: (CarMake) -> Unit,
    viewModel: CarMakeViewModel = hiltViewModel(),
) {
    val carMakes = viewModel.carMakes.collectAsState()
    val isLoading = viewModel.loadingState.collectAsState()
    val error = viewModel.errorState.collectAsState()


    Box(Modifier.fillMaxSize()) {
        CarMakeList(
            carMakes = carMakes.value,
            viewModel = viewModel,
            onMakeClick = onMakeClick,
            onEditClick = onEditClick,
        )

        if (isLoading.value) {
            LoadingDialog()
        }

        error.value?.let { message ->
            ErrorDialog(errorMsg = message)
        }
    }
}
