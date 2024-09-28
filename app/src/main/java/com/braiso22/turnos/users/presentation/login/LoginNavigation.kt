package com.braiso22.turnos.users.presentation.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.braiso22.turnos.TasksScreenTab
import com.braiso22.turnos.common.navigateWithoutBack
import com.braiso22.turnos.users.presentation.select_user.SelectUser
import kotlinx.serialization.Serializable

@Serializable
internal data object Login

fun NavGraphBuilder.login(
    navController: NavController,
) {
    composable<Login> {
        LoginScreen(
            navigateToTasks = { navController.navigateToTasks() },
            navigateToUserNameSelection = {
                navController.navigateToSelectUserName()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun NavController.navigateToTasks() = navigateWithoutBack(TasksScreenTab)
fun NavController.navigateToSelectUserName() = navigateWithoutBack(SelectUser)
