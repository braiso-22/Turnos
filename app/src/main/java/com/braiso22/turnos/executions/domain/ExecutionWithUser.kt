package com.braiso22.turnos.executions.domain

import com.braiso22.turnos.users.domain.User

data class ExecutionWithUser(
    val execution: Execution,
    val user: User?,
)