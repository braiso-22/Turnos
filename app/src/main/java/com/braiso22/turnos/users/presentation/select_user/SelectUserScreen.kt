package com.braiso22.turnos.users.presentation.select_user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.braiso22.turnos.R

@Composable
fun SelectUserScreen(
    navigateToTasks: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SelectUserViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                SelectUserViewModel.UiEvent.OnBadRegister -> {
                    snackbarHostState.showSnackbar(
                        context.getString(
                            R.string.select_other_username
                        )
                    )
                }

                SelectUserViewModel.UiEvent.OnRegister -> {
                    navigateToTasks()
                }
            }
        }
    }

    val username: String by viewModel.username.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    SelectUserComponent(
        username,
        isLoading,
        onChangeUsername = { viewModel.onChangeUsername(it) },
        onSelect = { viewModel.onSelect() },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun SelectUserComponent(
    username: String,
    isLoading: Boolean,
    onChangeUsername: (String) -> Unit,
    onSelect: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(64.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = onChangeUsername,
                label = {
                    Text(text = stringResource(R.string.user_name))
                },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!isLoading) {
                            onSelect()
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(8.dp))
            Button(
                onClick = {
                    if (!isLoading) {
                        onSelect()
                    }
                },
                modifier = Modifier
            ) {
                Text(stringResource(R.string.select))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectUserComponentPreview() {
    var state by remember {
        mutableStateOf(
            ""
        )
    }
    SelectUserComponent(
        username = state,
        isLoading = false,
        onChangeUsername = { state = it },
        onSelect = {},
        snackbarHostState = SnackbarHostState(),
        modifier = Modifier.fillMaxSize()
    )
}