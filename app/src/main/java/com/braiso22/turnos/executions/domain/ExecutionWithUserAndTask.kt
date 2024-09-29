package com.braiso22.turnos.executions.domain

import com.braiso22.turnos.tasks.domain.Task
import com.braiso22.turnos.users.domain.User

data class ExecutionWithUserAndTask(
    val execution: Execution,
    val user: User?,
    val task: Task?,
)