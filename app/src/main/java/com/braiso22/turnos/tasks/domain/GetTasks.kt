package com.braiso22.turnos.tasks.domain

import kotlinx.coroutines.flow.Flow

class GetTasks(
    private val tasksRepository: TasksRepository,
) {
    operator fun invoke(): Flow<List<Task>> {
        return tasksRepository.getTasks()
    }
}