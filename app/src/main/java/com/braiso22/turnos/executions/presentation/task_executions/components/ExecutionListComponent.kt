package com.braiso22.turnos.executions.presentation.task_executions.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.braiso22.turnos.R

@Composable
fun SameTypeExecutionListComponent(
    state: List<ExecutionUiState>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier
    ) {
        if (state.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_pending_receipts),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        items(state) {
            SameTypeExecutionComponent(
                state = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExecutionListComponentPreview() {

    SameTypeExecutionListComponent(
        state = List(4) {
            ExecutionUiState(
                id = "1",
                imageUrl = null,
                userName = "Braiso22",
                date = "28/03/2002",
                time = "10:00",
                isConfirmed = it % 2 == 0
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}
