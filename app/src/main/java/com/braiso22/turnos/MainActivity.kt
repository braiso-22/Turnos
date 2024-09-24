package com.braiso22.turnos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.braiso22.turnos.receipts.presentation.history.receiptsHistory
import com.braiso22.turnos.receipts.presentation.open.OpenReceipts
import com.braiso22.turnos.receipts.presentation.open.openReceipts
import com.braiso22.turnos.tasks.presentation.add.addTask
import com.braiso22.turnos.tasks.presentation.detail.taskDetail
import com.braiso22.turnos.tasks.presentation.edit.editTask
import com.braiso22.turnos.tasks.presentation.list.TasksList
import com.braiso22.turnos.tasks.presentation.list.taskList
import com.braiso22.turnos.ui.theme.TurnosTheme
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val analytics = FirebaseAnalytics.getInstance(this)
            analytics.logEvent("MainActivity", null)
            TurnosTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = TasksScreenTab) {
                    navigation<TasksScreenTab>(
                        startDestination = TasksList
                    ) {
                        taskList(navController)
                        editTask()
                        taskDetail(navController)
                        addTask(navController)
                    }
                    navigation<ReceiptsScreenTab>(
                        startDestination = OpenReceipts
                    ) {
                        openReceipts(navController)
                        receiptsHistory(navController)
                    }
                }
            }
        }
    }
}

@Serializable
data object TasksScreenTab

@Serializable
data object ReceiptsScreenTab