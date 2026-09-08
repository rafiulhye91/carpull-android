package com.example.carpull.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.carpull.presentation.CarMakeViewModel
import com.example.carpull.presentation.model.CarMake
import com.example.carpull.presentation.model.SortOrder
import com.example.carpull.view.components.CarMakeList
import com.example.carpull.view.components.ErrorDialog
import com.example.carpull.view.components.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMakeClick: (CarMake) -> Unit,
    onEditClick: (CarMake) -> Unit,
    onAddClick: () -> Unit,
    viewModel: CarMakeViewModel = hiltViewModel(),
) {
    val carMakes = viewModel.carMakes.collectAsState()
    val isLoading = viewModel.loadingState.collectAsState()
    val error = viewModel.errorState.collectAsState()
    val sortOrder = viewModel.sortOrder.collectAsState()

    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Makes") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Sort",
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        SortOrder.entries.forEach { order ->
                            DropdownMenuItem(
                                text = { Text(order.label) },
                                onClick = {
                                    viewModel.onSortOrderChange(order)
                                    menuExpanded = false
                                },
                                trailingIcon = {
                                    if (order == sortOrder.value) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                        )
                                    }
                                },
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add make",
                )
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            CarMakeList(
                carMakes = carMakes.value,
                viewModel = viewModel,
                onMakeClick = onMakeClick,
                onEditClick = onEditClick,
                modifier = Modifier
                    .padding(innerPadding)
            )

            if (isLoading.value) {
                LoadingDialog()
            }

            error.value?.let { message ->
                ErrorDialog(errorMsg = message)
            }
        }
    }
}
