package com.braiso22.turnos.tasks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.braiso22.turnos.tasks.domain.Task

@Entity
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val name: String,
) {
    fun toDomain(): Task = Task(
        id = id,
        name = name,
    )
}

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    name = name,
)
