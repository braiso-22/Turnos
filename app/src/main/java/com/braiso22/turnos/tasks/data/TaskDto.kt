package com.braiso22.turnos.tasks.data

import com.braiso22.turnos.tasks.domain.Task

data class TaskDto(
    val id: String = "",
    val name: String = "",
) {
    fun toDomain(): Task = Task(
        id = id,
        name = name,
    )
}