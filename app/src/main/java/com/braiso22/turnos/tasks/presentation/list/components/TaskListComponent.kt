package com.braiso22.turnos.tasks.presentation.list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.braiso22.turnos.R

@Composable
fun TaskListComponent(
    state: List<TaskUiState>,
    onClick: (String) -> Unit,
    onClickEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        if (state.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_tasks),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        itemsIndexed(state) { index, task ->
            TaskComponent(
                state = task,
                onClick = onClick,
                onClickEdit = onClickEdit,
                modifier = Modifier
            )
            if (index < state.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListComponentPreview() {
    TaskListComponent(
        state = List(3) {
            TaskUiState(
                id = it.toString(),
                name = "Take away stinky $it"
            )
        },

        onClick = {},
        onClickEdit = {},
        modifier = Modifier.fillMaxSize()
    )
}