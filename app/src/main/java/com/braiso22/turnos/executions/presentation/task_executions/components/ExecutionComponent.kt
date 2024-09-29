package com.braiso22.turnos.executions.presentation.task_executions.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material3.Icon
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

data class ExecutionUiState(
    val id: String,
    val imageUrl: String? = null,
    val userName: String,
    val date: String,
    val time: String,
    val isConfirmed: Boolean,
)

@Composable
fun SameTypeExecutionComponent(
    state: ExecutionUiState,
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
        )
        Spacer(Modifier.padding(8.dp))
        Column(
            Modifier
                .weight(1f)
        ) {
            Text(
                text = state.userName,
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
                    text = state.date,
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

        Icon(
            imageVector = if (state.isConfirmed) {
                Icons.Outlined.CheckBox
            } else {
                Icons.Default.CheckBoxOutlineBlank
            },
            contentDescription = null,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExecutionComponentPreview() {
    TurnosTheme {
        SameTypeExecutionComponent(
            state = ExecutionUiState(
                id = "prodesset",
                imageUrl = null,
                userName = "Ralph Cardenas",
                date = "28/03/2002",
                time = "10:00",
                isConfirmed = false

            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}