package com.braiso22.turnos.tasks.presentation.list


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.braiso22.turnos.R
import com.braiso22.turnos.tasks.presentation.list.components.TaskListComponent
import com.braiso22.turnos.tasks.presentation.list.components.TaskUiState
import com.braiso22.turnos.ui.theme.TurnosTheme

@Composable
fun TaskListScreen(
    navigateToTask: (String) -> Unit,
    navigateToTaskEdit: (String) -> Unit,
    navigateToAddTask: () -> Unit,
    navigateToReceiptsTab: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                TaskListViewModel.UiEvent.NavigateToAddTask -> {
                    navigateToAddTask()
                }

                is TaskListViewModel.UiEvent.NavigateToEditTask -> {
                    navigateToTaskEdit(event.id)
                }

                is TaskListViewModel.UiEvent.NavigateToTask -> {
                    navigateToTask(event.id)
                }

                TaskListViewModel.UiEvent.NavigateToReceipts -> {
                    navigateToReceiptsTab()
                }
            }
        }
    }

    val tasks by viewModel.tasks.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    TaskListScreenComponent(
        tasks = tasks,
        searchText = searchText,
        onTaskEvent = {
            viewModel.onEvent(it)
        },
        modifier = modifier,
    )
}

sealed class TasksScreenEvent {
    data object OnClickNew : TasksScreenEvent()
    data class OnClickTask(val id: String) : TasksScreenEvent()
    data class OnClickEdit(val id: String) : TasksScreenEvent()
    data class OnChangedSearch(val search: String) : TasksScreenEvent()
    data object OnClickReceipts : TasksScreenEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreenComponent(
    tasks: List<TaskUiState>,
    searchText: String,
    onTaskEvent: (TasksScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tasks),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            )
        },
        floatingActionButton = {

            // TODO check if we need this button
            /*FloatingActionButton(
                onClick = {
                    onTaskEvent(TasksScreenEvent.OnClickNew)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_task_button)
                )
            }*/
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
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
                    selected = false,
                    onClick = {
                        onTaskEvent(TasksScreenEvent.OnClickReceipts)
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    onTaskEvent(TasksScreenEvent.OnChangedSearch(it))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            TaskListComponent(
                state = tasks,
                onClick = {
                    onTaskEvent(TasksScreenEvent.OnClickTask(it))
                },
                onClickEdit = {
                    onTaskEvent(TasksScreenEvent.OnClickEdit(it))
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TasksListScreenComponentPreview() {
    TurnosTheme {
        TaskListScreenComponent(
            tasks = List(3) {
                TaskUiState(
                    id = it.toString(),
                    name = "Take away stinky $it"
                )
            },
            searchText = "",
            onTaskEvent = {

            },
            modifier = Modifier.fillMaxSize()
        )
    }
}