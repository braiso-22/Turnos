package com.braiso22.turnos.tasks.domain

class SyncTasks(
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke() {
        tasksRepository.listenOnlineTasks().collect {
            tasksRepository.syncTasks(it)
        }
    }
}