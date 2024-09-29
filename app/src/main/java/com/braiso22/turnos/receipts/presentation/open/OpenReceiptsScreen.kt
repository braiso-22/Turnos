package com.braiso22.turnos.receipts.presentation.open

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.braiso22.turnos.R
import com.braiso22.turnos.receipts.presentation.open.components.ReceiptListComponent
import com.braiso22.turnos.receipts.presentation.open.components.ReceiptUiState
import com.braiso22.turnos.receipts.presentation.open.components.toUi
import com.braiso22.turnos.ui.theme.TurnosTheme
import java.time.LocalDate

@Composable
fun OpenReceiptsScreen(
    navigateToHistory: () -> Unit,
    navigateToTasks: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OpenReceiptsViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.onInit()

        viewModel.eventFlow.collect { event ->
            when (event) {
                OpenReceiptsViewModel.UiEvent.NavigateToHistory -> {
                    navigateToHistory()
                }

                OpenReceiptsViewModel.UiEvent.NavigateToTasks -> {
                    navigateToTasks()
                }
            }
        }
    }

    val receipts by viewModel.receipts.collectAsState()

    OpenReceiptsScreenComponent(
        state = receipts,
        onEvent = {
            viewModel.onEvent(it)
        },
        modifier = modifier
    )
}

sealed class OpenReceiptEvent {
    data object OnClickHistory : OpenReceiptEvent()
    data object OnClickTasks : OpenReceiptEvent()
    data class OnClickConfirm(val id: String) : OpenReceiptEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenReceiptsScreenComponent(
    state: Map<String, List<ReceiptUiState>>,
    onEvent: (OpenReceiptEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.open_receipts),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(OpenReceiptEvent.OnClickHistory) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = stringResource(R.string.history),
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { onEvent(OpenReceiptEvent.OnClickTasks) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Task,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.tasks),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {
                        // intended to be empty its the same component
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Pending,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.open_receipts),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                )

            }
        },
        modifier = modifier,
    ) { paddingValues ->
        ReceiptListComponent(
            state = state,
            onClickConfirm = {
                onEvent(OpenReceiptEvent.OnClickConfirm(it))
            },
            modifier = Modifier.padding(paddingValues),
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun OpenReceiptsScreenComponentPreview() {
    val day = LocalDate.now()
    TurnosTheme {
        OpenReceiptsScreenComponent(
            state = mapOf(
                day.toUi() to List(3) {
                    ReceiptUiState(
                        id = "1",
                        taskName = "Take away the stinky",
                        userName = "Braiso22",
                        time = "1$it:00",
                    )
                },
                day.plusDays(1).toUi() to List(2) {
                    ReceiptUiState(
                        id = "1",
                        taskName = "Take away the stinky",
                        userName = "Braiso22",
                        time = "1$it:00",
                    )
                }
            ),
            onEvent = {

            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
