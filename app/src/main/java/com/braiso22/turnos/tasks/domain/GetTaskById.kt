package com.braiso22.turnos.tasks.domain

class GetTaskById(
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke(taskId: String): Task? {
        return tasksRepository.getTaskById(taskId)
    }

}