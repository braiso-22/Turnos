package com.braiso22.turnos.receipts.presentation.open.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.braiso22.turnos.tasks.presentation.components.ProfilePictureComponent
import com.braiso22.turnos.ui.theme.TurnosTheme

data class ReceiptUiState(
    val id: String,
    val imageUrl: String? = null,
    val taskName: String,
    val userName: String,
    val time: String,
)

@Composable
fun ReceiptComponent(
    state: ReceiptUiState,
    onClickConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProfilePictureComponent(
            user = state.userName,
            imageUrl = state.imageUrl,
            modifier = Modifier
                .padding(8.dp)
        )
        Column(
            Modifier
                .weight(1f)
        ) {
            Text(
                text = state.taskName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.userName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = state.time,
                )
            }
        }

        Button(
            onClick = {
                onClickConfirm(state.id)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Confirm")
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptComponentPreview() {
    TurnosTheme {
        ReceiptComponent(
            state = ReceiptUiState(
                id = "1",
                taskName = "Take away the stinky",
                userName = "Braiso22",
                time = "10:00",
            ),
            onClickConfirm = {},
            modifier = Modifier
        )
    }
}