package com.braiso22.turnos.executions.domain.use_cases

import com.braiso22.turnos.executions.domain.ExecutionWithUser
import com.braiso22.turnos.executions.domain.ExecutionsRepository
import com.braiso22.turnos.users.domain.GetUserById
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetExecutionsByTaskId(
    private val executionsRepository: ExecutionsRepository,
    private val getUserById: GetUserById,
) {
    operator fun invoke(taskId: String): Flow<List<ExecutionWithUser>> {
        return executionsRepository.getExecutionsByTaskIds(taskId).map { executions ->
            executions.map {
                ExecutionWithUser(
                    execution = it,
                    user = getUserById(it.userId)
                )
            }
        }
    }
}