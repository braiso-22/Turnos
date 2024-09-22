package com.braiso22.turnos.tasks.presentation.add

import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.braiso22.turnos.R

@Composable
fun AddTaskScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTaskViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                AddTaskViewModel.UiEvent.NavigateBack -> {
                    navigateBack()
                }

                AddTaskViewModel.UiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error_saving_task)
                    )
                }
            }
        }
    }

    val name by viewModel.name.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    AddTaskScreenComponent(
        name = name,
        isLoading = isLoading,
        snackbarHostState = snackbarHostState,
        onEvent = { viewModel.onEvent(it) },
        modifier = modifier
    )
}

sealed class AddTaskEvent {
    data object OnClickSave : AddTaskEvent()
    data object OnClickCancel : AddTaskEvent()
    data class OnChangedName(val name: String) : AddTaskEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreenComponent(
    name: String,
    isLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onEvent: (AddTaskEvent) -> Unit,
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
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onEvent(AddTaskEvent.OnClickCancel)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = SpaceBetween
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    onEvent(AddTaskEvent.OnChangedName(it))
                },
                label = {
                    Text(stringResource(R.string.name))
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            Button(
                onClick = {
                    if (!isLoading) {
                        onEvent(AddTaskEvent.OnClickSave)
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTaskScreenComponentPreview() {

    AddTaskScreenComponent(
        name = "",
        isLoading = false,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}