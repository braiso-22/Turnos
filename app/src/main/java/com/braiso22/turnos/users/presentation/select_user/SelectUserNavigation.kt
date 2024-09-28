package com.braiso22.turnos.users.presentation.select_user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.braiso22.turnos.users.presentation.login.navigateToTasks
import kotlinx.serialization.Serializable

@Serializable
internal data object SelectUser

fun NavGraphBuilder.selectUser(
    navController: NavController,
) {
    composable<SelectUser> {
        SelectUserScreen(
            navigateToTasks = { navController.navigateToTasks() },
            modifier = Modifier.fillMaxSize()
        )
    }
}