package com.braiso22.turnos.tasks.domain

import com.braiso22.turnos.common.Resource
import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    fun saveTask(task: Task): Flow<Resource<Task>>
    fun getTasks(): Flow<List<Task>>
}