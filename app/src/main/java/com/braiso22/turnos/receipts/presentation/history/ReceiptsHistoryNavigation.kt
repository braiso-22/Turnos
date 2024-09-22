package com.braiso22.turnos.receipts.presentation.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object ReceiptsHistory

fun NavGraphBuilder.receiptsHistory(
    navController: NavController,
) {
    composable<ReceiptsHistory> { navBackStackEntry ->

    }
}
