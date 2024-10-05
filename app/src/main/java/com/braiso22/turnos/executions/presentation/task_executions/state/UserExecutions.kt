package com.braiso22.turnos.executions.presentation.task_executions.state

data class UserExecutions(
    val userId: String,
    val username: String,
    val executions: Int,
    val selected: Boolean,
)
