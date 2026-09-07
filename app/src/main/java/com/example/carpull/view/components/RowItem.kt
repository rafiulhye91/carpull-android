package com.example.carpull.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.carpull.presentation.model.CarMake
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class RowState { Closed, Open }

private val ActionWidth = 72.dp

@Composable
fun SwipeableRowItem(
    carMake: CarMake,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val actionsWidthPx = with(LocalDensity.current) { (ActionWidth * 2).toPx() }
    val scope = rememberCoroutineScope()

    val dragState = remember(actionsWidthPx) {
        AnchoredDraggableState(
            initialValue = RowState.Closed,
            anchors = DraggableAnchors {
                RowState.Closed at 0f
                RowState.Open at -actionsWidthPx
            },
        )
    }

    fun close() {
        scope.launch { dragState.animateTo(RowState.Closed) }
    }

    Box(modifier.fillMaxWidth()) {

        // Revealed behind the row.
        Row(
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.End,
        ) {
            ActionButton(
                icon = Icons.Default.Edit,
                label = "Edit",
                container = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { close(); onEdit() },
            )
            ActionButton(
                icon = Icons.Default.Delete,
                label = "Delete",
                container = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                onClick = { close(); onDelete() },
            )
        }

        // The row, offset by the drag.
        RowItem(
            carMake = carMake,
            onClick = {
                if (dragState.currentValue == RowState.Open) close() else onClick()
            },
            modifier = Modifier
                .offset { IntOffset(dragState.requireOffset().roundToInt(), 0) }
                .anchoredDraggable(
                    state = dragState,
                    reverseDirection = false,
                    orientation = Orientation.Horizontal,
                ),
        )
    }
}

@Composable
private fun RowItem(
    carMake: CarMake,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = carMake.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    container: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(ActionWidth)
            .fillMaxHeight()
            .background(container)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor)
    }
}

private val sampleMake = CarMake(
    id = 1L,
    name = "Aston Martin",
    isEdited = false,
    isDeleted = false,
)

@Preview(showBackground = true)
@Composable
private fun PreviewSwipeableRowItem() {
    MaterialTheme {
        SwipeableRowItem(
            carMake = sampleMake.copy(name = "Ford"),
            onClick = {},
            onEdit = {},
            onDelete = {},
        )
    }
}
