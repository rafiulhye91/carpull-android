package com.example.carpull.view.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.carpull.presentation.CarMakeViewModel
import com.example.carpull.presentation.model.CarMake

@Composable
fun CarMakeList(
    carMakes: List<CarMake>,
    onMakeClick: (CarMake) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CarMakeViewModel,
) {
    LazyColumn(modifier = modifier) {
        items(
            items = carMakes,
            key = { it.id },
        ) { carMake ->
            SwipeableRowItem(
                carMake = carMake,
                onClick = { onMakeClick(carMake) },
                onEdit = {viewModel.onItemEdit(carMake)},
                onDelete = {viewModel.onItemDelete(carMake)},
            )
            HorizontalDivider()
        }
    }
}
