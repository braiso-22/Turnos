package com.braiso22.turnos.tasks.presentation.list.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class TaskUiState(
    val id: String,
    val name: String,
)

@Composable
fun TaskComponent(
    state: TaskUiState,
    onClick: (String) -> Unit,
    onClickEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .clickable { onClick(state.id) }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = state.name,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                onClick = { onClickEdit(state.id) },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit button"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskComponentPreview() {

    TaskComponent(
        state = TaskUiState(
            id = "1",
            name = "Recoger la basura mucha mucha mucha basura"
        ),
        onClick = {},
        onClickEdit = {},
        modifier = Modifier.width(360.dp)
    )
}