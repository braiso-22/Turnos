package com.braiso22.turnos.tasks.presentation.detail

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.braiso22.turnos.R
import com.braiso22.turnos.executions.presentation.same_type.ExecutionUiState
import com.braiso22.turnos.executions.presentation.same_type.SameTypeExecutionListComponent

@Composable
fun TaskDetailScreen(
    id: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(id) {
        viewModel.onInit(id)
    }

    val executions by viewModel.executions.collectAsState()
    TaskDetailScreenComponent(
        executions = executions,
        navigateBack = navigateBack,
        onClickNew = { viewModel.onClickNew(id) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreenComponent(
    executions: List<ExecutionUiState>,
    navigateBack: () -> Unit,
    onClickNew: () -> Unit,
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
private fun TaskDetailScreenComponentPreview() {
    TaskDetailScreenComponent(
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
        modifier = Modifier.fillMaxSize()
    )
}