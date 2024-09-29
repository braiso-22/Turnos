package com.braiso22.turnos.tasks.presentation.list

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.braiso22.turnos.common.navigateWithoutBack
import com.braiso22.turnos.executions.presentation.open_executions.OpenReceipts
import com.braiso22.turnos.tasks.presentation.add.AddTask
import com.braiso22.turnos.executions.presentation.task_executions.TaskExecutions
import com.braiso22.turnos.tasks.presentation.edit.EditTask
import kotlinx.serialization.Serializable

@Serializable
internal data object TasksList

fun NavGraphBuilder.taskList(
    navController: NavController,
) {
    composable<TasksList> { _ ->
        TaskListScreen(
            navigateToTask = { navController.navigateToDetail(it) },
            navigateToTaskEdit = { navController.navigateToEdit(it) },
            navigateToAddTask = { navController.navigateToAdd() },
            navigateToReceiptsTab = { navController.navigateToReceipts() },
        )
    }
}

fun NavController.navigateToEdit(id: String) = navigate(EditTask(id))
fun NavController.navigateToDetail(id: String) = navigate(TaskExecutions(id))
fun NavController.navigateToAdd() = navigate(AddTask)
fun NavController.navigateToReceipts() = navigateWithoutBack(OpenReceipts)