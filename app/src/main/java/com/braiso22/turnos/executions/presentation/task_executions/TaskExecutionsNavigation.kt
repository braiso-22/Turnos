package com.braiso22.turnos.executions.presentation.task_executions

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
internal data class TaskExecutions(val id: String)

fun NavGraphBuilder.taskExecutions(
    navController: NavController,
) {
    composable<TaskExecutions> { navBackStackEntry ->
        val data = navBackStackEntry.toRoute<TaskExecutions>()
        TaskExecutionsScreen(data.id, navigateBack = {
            navController.popBackStack()
        })
    }
}