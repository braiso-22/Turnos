package com.braiso22.turnos.executions.presentation.executions_history

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.braiso22.turnos.R
import kotlinx.serialization.Serializable

@Serializable
internal data object ReceiptsHistory

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.receiptsHistory(
    navController: NavController,
) {
    composable<ReceiptsHistory> { _ ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.history),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.go_back)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Text(
                stringResource(R.string.not_implemented),
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
