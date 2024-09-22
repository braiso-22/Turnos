package com.braiso22.turnos.tasks.presentation.add

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object AddTask

fun NavGraphBuilder.addTask(navController: NavController) {
    composable<AddTask> { navBackStackEntry ->
        AddTaskScreen(
            navigateBack = { navController.popBackStack() }
        )
    }
}