package com.braiso22.turnos.users.presentation.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.braiso22.turnos.R
import com.braiso22.turnos.ui.theme.TurnosTheme
import com.braiso22.turnos.users.presentation.login.components.EmailPasswordComponent

@Composable
fun LoginScreen(
    navigateToTasks: () -> Unit,
    navigateToUserNameSelection: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    suspend fun showSnackBar(id: Int) {
        snackBarHostState.showSnackbar(
            message = context.getString(id)
        )
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                LoginViewModel.UiEvent.OnBadLogin -> {
                    showSnackBar(R.string.bad_credentials)
                }

                LoginViewModel.UiEvent.OnLogin -> {
                    navigateToTasks()
                }

                LoginViewModel.UiEvent.NoInternet -> {
                    showSnackBar(R.string.couldn_t_reach_server_check_your_internet_connection)

                }

                LoginViewModel.UiEvent.OnBadRegister -> {
                    showSnackBar(R.string.could_t_register_with_this_email)
                }

                LoginViewModel.UiEvent.OnRegister -> {
                    navigateToUserNameSelection()
                }
            }
        }
    }


    val email: String by viewModel.email.collectAsState()
    val password: String by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    LoginComponent(
        email = email,
        password = password,
        isLoading = isLoading,
        onEvent = { viewModel.onEvent(it) },
        snackbarHostState = snackBarHostState,
        modifier = modifier,
    )
}

sealed class LoginScreenEvent {
    data object OnLogin : LoginScreenEvent()
    data object OnRegister : LoginScreenEvent()
    data class OnEmailChange(val email: String) : LoginScreenEvent()
    data class OnPasswordChange(val password: String) : LoginScreenEvent()
}

@Composable
fun LoginComponent(
    email: String,
    password: String,
    isLoading: Boolean,
    onEvent: (LoginScreenEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        modifier = modifier,
    ) { paddingValues ->
        EmailPasswordComponent(
            email = email,
            password = password,
            isLoading = isLoading,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginComponentPreview() {

    TurnosTheme {
        LoginComponent(
            email = "",
            password = "",
            isLoading = false,
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
            modifier = Modifier.fillMaxSize()
        )
    }
}