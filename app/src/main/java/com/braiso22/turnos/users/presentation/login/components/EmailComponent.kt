package com.braiso22.turnos.users.presentation.login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.braiso22.turnos.R

@Composable
fun EmailComponent(
    email: String,
    onValueChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        email,
        onValueChange = onValueChange,
        label = {
            Text(stringResource(R.string.email))
        },
        trailingIcon = {
            if (email.isNotBlank()) {
                IconButton(
                    onClick = {
                        onValueChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_text)
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() }
        ),
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun EmailComponentComponentPreview() {
    var state by remember {
        mutableStateOf(
            ""
        )
    }
    EmailComponent(
        email = state,
        onValueChange = {
            state = it
        },
        onNext = {},
        modifier = Modifier.fillMaxWidth()
    )
}