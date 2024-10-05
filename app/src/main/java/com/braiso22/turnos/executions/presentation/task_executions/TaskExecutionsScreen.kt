package com.braiso22.turnos.executions.presentation.task_executions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.braiso22.turnos.R
import com.braiso22.turnos.executions.presentation.task_executions.components.ExecutionUiState
import com.braiso22.turnos.executions.presentation.task_executions.components.SameTypeExecutionListComponent
import com.braiso22.turnos.executions.presentation.task_executions.state.UserExecutions
import com.braiso22.turnos.ui.theme.TurnosTheme

@Composable
fun TaskExecutionsScreen(
    id: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskExecutionsViewModel = hiltViewModel(),
) {
    val snackbarHostState = SnackbarHostState()
    val context = LocalContext.current
    LaunchedEffect(id) {
        viewModel.onInit(id)
        viewModel.eventFlow.collect { event ->
            when (event) {
                TaskExecutionsViewModel.UiEvent.NavigateBack -> {
                    navigateBack()
                }

                TaskExecutionsViewModel.UiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.couldn_t_add_execution)
                    )
                }
            }
        }
    }

    val executions by viewModel.executions.collectAsState()
    val userExecutions by viewModel.userExecutions.collectAsState()
    TaskExecutionsScreenComponent(
        executions = executions,
        userExecutions = userExecutions,
        onClickFilter = { viewModel.onClickFilter(it) },
        navigateBack = { viewModel.clickBack() },
        onClickNew = { viewModel.onClickNew(id) },
        hostState = snackbarHostState,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskExecutionsScreenComponent(
    executions: List<ExecutionUiState>,
    userExecutions: List<UserExecutions>,
    onClickFilter: (String) -> Unit,
    navigateBack: () -> Unit,
    onClickNew: () -> Unit,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.executions),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }

            )
        },
        snackbarHost = {
            SnackbarHost(hostState)
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(userExecutions) {
                    FilterChip(
                        selected = it.selected,
                        onClick = { onClickFilter(it.userId) },
                        label = {
                            Text(text = "${it.username}: ${it.executions}")
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
            HorizontalDivider()
            SameTypeExecutionListComponent(
                state = executions,
                modifier = Modifier.weight(1f)
            )
            var isEnabled by rememberSaveable {
                mutableStateOf(true)
            }
            Button(
                onClick = {
                    onClickNew()
                    isEnabled = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                enabled = isEnabled
            ) {
                Text(text = stringResource(R.string.new_execution))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskExecutionsScreenComponentPreview() {
    TurnosTheme {
        TaskExecutionsScreenComponent(
            executions = List(10) {
                ExecutionUiState(
                    id = "dolor",
                    imageUrl = null,
                    userName = "Chrystal Hodges",
                    date = "salutatus",
                    time = "bibendum",
                    isConfirmed = false

                )
            },
            userExecutions = List(10) {
                UserExecutions(
                    username = "Braiso22",
                    userId = "dolor",
                    executions = 10,
                    selected = false,
                )
            },
            navigateBack = {},
            onClickNew = {},
            onClickFilter = {},
            hostState = SnackbarHostState(),
            modifier = Modifier.fillMaxSize()
        )
    }
}
