package com.braiso22.turnos.executions.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.braiso22.turnos.executions.domain.Execution
import java.time.LocalDateTime

@Entity
data class ExecutionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val taskId: String,
    val date: LocalDateTime,
    val confirmed: Boolean,
) {
    fun toDomain(): Execution = Execution(
        id = id,
        userId = userId,
        taskId = taskId,
        dateTime = date,
        isConfirmed = confirmed,
    )
}

fun Execution.toEntity(): ExecutionEntity = ExecutionEntity(
    id = id,
    userId = userId,
    taskId = taskId,
    date = dateTime,
    confirmed = isConfirmed,
)
