package com.example.carpull.view.components

import android.app.Dialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
        ) {

            Row(
                modifier = Modifier
                    .padding(32.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Loading...",
                    modifier = Modifier.weight(4f),
                )
            }

        }
    }
}

@Composable
fun ErrorDialog(
    errorMsg: String = "No details!"
) {

    val openDialog = rememberSaveable {
        mutableStateOf(true)
    }

    if (openDialog.value) {
        AlertDialog(
            title = {
                Text(text = "Error")
            },
            text = {
                Text(text = errorMsg)
            },
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Ok")
                }
            },
        )
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingDialog() {
    LoadingDialog()
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorDialog() {
    ErrorDialog()
}