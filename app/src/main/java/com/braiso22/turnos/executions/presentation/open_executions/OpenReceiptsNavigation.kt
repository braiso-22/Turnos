package com.braiso22.turnos.executions.presentation.open_executions

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.braiso22.turnos.TasksScreenTab
import com.braiso22.turnos.common.navigateWithoutBack
import com.braiso22.turnos.executions.presentation.executions_history.ReceiptsHistory
import kotlinx.serialization.Serializable

@Serializable
internal data object OpenReceipts

fun NavGraphBuilder.openReceipts(
    navController: NavController,
) {
    composable<OpenReceipts> { _ ->
        OpenReceiptsScreen(
            navigateToHistory = { navController.navigateToHistory() },
            navigateToTasks = { navController.navigateToTasks() }
        )
    }
}

fun NavController.navigateToTasks() = navigateWithoutBack(TasksScreenTab)
fun NavController.navigateToHistory() = navigate(ReceiptsHistory)
