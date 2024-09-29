package com.braiso22.turnos.executions.domain

import com.braiso22.turnos.common.Resource
import kotlinx.coroutines.flow.Flow

interface ExecutionsRepository {
    fun saveExecution(execution: Execution): Flow<Resource<Execution>>
    fun getExecutions(): Flow<List<Execution>>
    fun getExecutionsByTaskIds(taskId: String): Flow<List<Execution>>
    fun listenOnlineExecutions(): Flow<List<Execution>>
    suspend fun syncExecutions(executions: List<Execution>)
    fun getExecutionsByOtherUsers(excludeUserId: String): Flow<List<Execution>>
    fun confirmExecution(executionId: String): Flow<Resource<Unit>>
}