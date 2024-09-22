package com.braiso22.turnos.tasks.presentation.detail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data class TaskDetail(val id: String)

fun NavGraphBuilder.taskDetail() {
    composable<TaskDetail> { navBackStackEntry ->

    }
}