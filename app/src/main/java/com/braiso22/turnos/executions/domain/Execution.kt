package com.braiso22.turnos.executions.domain

import java.time.LocalDateTime

data class Execution(
    val id: String,
    val dateTime: LocalDateTime,
    val isConfirmed: Boolean,
    val taskId: String,
    val userId: String,
)
