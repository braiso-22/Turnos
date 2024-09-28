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
import com.braiso22.turnos.users.presentation.login.Login
import com.braiso22.turnos.users.presentation.login.login
import com.braiso22.turnos.users.presentation.select_user.selectUser
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
            var isLogged = false
            Firebase.auth.currentUser?.let {
                isLogged = true
            }
            TurnosTheme {
                val navController = rememberNavController()
                NavHost(
                    navController,
                    startDestination = if (isLogged) TasksScreenTab else Login
                ) {
                    login(navController)
                    selectUser(navController)

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