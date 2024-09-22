package com.braiso22.turnos.tasks.presentation.edit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data class EditTask(val id: String)

fun NavGraphBuilder.editTask() {
    composable<EditTask> { navBackStackEntry ->

    }
}