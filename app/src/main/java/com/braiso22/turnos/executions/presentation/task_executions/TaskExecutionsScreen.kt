package com.braiso22.turnos.executions.presentation.task_executions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.braiso22.turnos.R
import com.braiso22.turnos.executions.presentation.task_executions.components.ExecutionUiState
import com.braiso22.turnos.executions.presentation.task_executions.components.SameTypeExecutionListComponent
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
    TaskExecutionsScreenComponent(
        executions = executions,
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
        Box(modifier = Modifier.padding(paddingValues)) {
            SameTypeExecutionListComponent(
                state = executions,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp)
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
                    .align(Alignment.BottomCenter)
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
            navigateBack = {},
            onClickNew = {},
            hostState = SnackbarHostState(),
            modifier = Modifier.fillMaxSize()
        )
    }
}
