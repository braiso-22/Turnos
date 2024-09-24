package com.braiso22.turnos.tasks.presentation.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
internal data class TaskDetail(val id: String)

fun NavGraphBuilder.taskDetail(
    navController: NavController,
) {
    composable<TaskDetail> { navBackStackEntry ->
        val data = navBackStackEntry.toRoute<TaskDetail>()
        TaskDetailScreen(data.id, navigateBack = {
            navController.popBackStack()
        })
    }
}