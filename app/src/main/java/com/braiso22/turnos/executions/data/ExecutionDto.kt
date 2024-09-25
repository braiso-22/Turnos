package com.braiso22.turnos.executions.data

import com.braiso22.turnos.executions.domain.Execution
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.time.ZoneId

data class ExecutionDto(
    val id: String? = null,
    val userId: String = "",
    val taskId: String = "",
    @ServerTimestamp
    val date: Timestamp = Timestamp.now(),
    val confirmed: Boolean = false,
) {
    fun toDomain(): Execution = Execution(
        id = id,
        dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
        isConfirmed = confirmed,
        taskId = taskId,
        userId = userId,
    )
}

fun Execution.toDto(): ExecutionDto = ExecutionDto(
    id = id,
    userId = userId,
    taskId = taskId,
    date = Timestamp.now(),
    confirmed = isConfirmed,
)