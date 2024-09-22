package com.braiso22.turnos.tasks.presentation.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.braiso22.turnos.ui.theme.TurnosTheme

sealed class LoginEvent {
    data class OnChangedUser(val user: String) : LoginEvent()
    data class OnChangedPassword(val password: String) : LoginEvent()
    data object OnClickLogin : LoginEvent()
    data object OnClickGoogleLogin : LoginEvent()
}

@Composable
fun LoginComponent(
    user: String,
    password: String,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = user,
            onValueChange = { onEvent(LoginEvent.OnChangedUser(it)) },
            label = { Text(text = "User") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = password,
            onValueChange = { onEvent(LoginEvent.OnChangedPassword(it)) },
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLines = 1,
        )

        Spacer(
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = { onEvent(LoginEvent.OnClickLogin) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }
        Button(
            onClick = { onEvent(LoginEvent.OnClickGoogleLogin) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
            )
            Spacer(
                modifier = Modifier.padding(8.dp)
            )
            Text(text = "Login with Google")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginComponentPreview() {
    TurnosTheme {
        LoginComponent(
            user = "",
            password = "",
            onEvent = {},
            modifier = Modifier
        )
    }
}