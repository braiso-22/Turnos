package com.braiso22.turnos.common

import androidx.navigation.NavController

fun NavController.navigateWithoutBack(destination: Any) {
    navigate(destination) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}