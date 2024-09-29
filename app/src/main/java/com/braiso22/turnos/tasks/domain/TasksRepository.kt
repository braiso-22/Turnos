package com.braiso22.turnos.tasks.domain

import com.braiso22.turnos.common.Resource
import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    fun saveTask(task: Task): Flow<Resource<Task>>
    fun listenOnlineTasks(): Flow<List<Task>>
    suspend fun syncTasks(tasks: List<Task>)
    fun getTasks(): Flow<List<Task>>
    suspend fun getTaskById(taskId: String): Task?
}