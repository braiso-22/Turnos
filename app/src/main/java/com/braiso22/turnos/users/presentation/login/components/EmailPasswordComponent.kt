package com.braiso22.turnos.users.presentation.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.braiso22.turnos.R
import com.braiso22.turnos.users.presentation.login.LoginScreenEvent

@Composable
fun EmailPasswordComponent(
    email: String,
    password: String,
    isLoading: Boolean,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmailComponent(
            email = email,
            onValueChange = {
                onEvent(
                    LoginScreenEvent.OnEmailChange(it)
                )
            },
            onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Spacer(Modifier.padding(8.dp))
        PasswordComponent(
            password,
            onValueChange = {
                onEvent(
                    LoginScreenEvent.OnPasswordChange(it)
                )
            },
            onDone = {
                focusManager.clearFocus()
                onEvent(
                    LoginScreenEvent.OnLogin
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.padding(8.dp))
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Button(
                onClick = {
                    onEvent(
                        LoginScreenEvent.OnRegister
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.register))
            }
            Button(
                onClick = {
                    onEvent(
                        LoginScreenEvent.OnLogin
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.login))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailPasswordComponentPreview() {
    EmailPasswordComponent(
        email = "a",
        password = "aa",
        onEvent = {},
        isLoading = false,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}