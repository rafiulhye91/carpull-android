package com.example.carpull.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.carpull.presentation.CarMakeFormViewModel
import com.example.carpull.presentation.model.CarMakeFormMode
import com.example.carpull.view.components.ErrorDialog
import com.example.carpull.view.components.LoadingDialog

@Composable
fun CarMakeFormScreen(
    onBack: () -> Unit,
    viewModel: CarMakeFormViewModel = hiltViewModel(),
) {
    val form = viewModel.form.collectAsState()
    val isLoading = viewModel.loadingState.collectAsState()
    val error = viewModel.errorState.collectAsState()
    val isSaved = viewModel.isSaved.collectAsState()

    LaunchedEffect(isSaved.value) {
        if (isSaved.value) {
            viewModel.onSavedHandled()
            onBack()
        }
    }

    val state = form.value

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
                Text(
                    text = when (viewModel.mode) {
                        CarMakeFormMode.Add -> "Add make"
                        CarMakeFormMode.Edit -> "Edit make"
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            HorizontalDivider()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("Name") },
                        isError = state.name.isBlank(),
                        singleLine = true,
                        supportingText = state.originalName?.let { { Text("vPIC name: $it") } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                    )
                }

                item {
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = viewModel::onNotesChange,
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item {
                    Text(
                        text = "Models",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = state.modelDraft,
                            onValueChange = viewModel::onModelDraftChange,
                            label = { Text("New model") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        Button(
                            onClick = viewModel::onAddModel,
                            enabled = state.modelDraft.isNotBlank(),
                        ) {
                            Text("Add")
                        }
                    }
                }

                itemsIndexed(state.newModelNames) { index, name ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { viewModel.onRemoveNewModel(index) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove $name",
                            )
                        }
                    }
                }

                items(state.existingModels, key = { it.id }) { carModel ->
                    Text(
                        text = carModel.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    )
                }

                item {
                    Button(
                        onClick = viewModel::onSave,
                        enabled = !isLoading.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        if (isLoading.value) {
            LoadingDialog()
        }

        error.value?.let { message ->
            ErrorDialog(errorMsg = message)
        }
    }
}