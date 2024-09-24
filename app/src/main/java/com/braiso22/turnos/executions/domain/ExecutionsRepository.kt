package com.braiso22.turnos.executions.domain

import com.braiso22.turnos.common.Resource
import kotlinx.coroutines.flow.Flow

interface ExecutionsRepository {
    fun saveExecution(execution: Execution): Flow<Resource<Execution>>
    fun getExecutions(): Flow<List<Execution>>
    fun getExecutionsByTaskIds(taskId: String): Flow<List<Execution>>
}