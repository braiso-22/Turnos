package com.braiso22.turnos.executions.domain.use_cases

import com.braiso22.turnos.executions.domain.ExecutionWithUserAndTask
import com.braiso22.turnos.executions.domain.ExecutionsRepository
import com.braiso22.turnos.tasks.domain.GetTaskById
import com.braiso22.turnos.users.domain.GetCurrentUser
import com.braiso22.turnos.users.domain.GetUserById
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetOpenExecutions(
    private val executionsRepository: ExecutionsRepository,
    private val getCurrentUser: GetCurrentUser,
    private val getUserById: GetUserById,
    private val getTaskById: GetTaskById,
) {
    operator fun invoke(): Flow<List<ExecutionWithUserAndTask>> = flow {
        val user = getCurrentUser()
        val userId = user?.id ?: throw IllegalStateException("User not found")

        executionsRepository.getExecutionsByOtherUsers(excludeUserId = userId)
            .map { executions -> executions.filter { !it.isConfirmed } }
            .collect { filteredExecutions ->
                val executionsWithUserAndTask = filteredExecutions.map {
                    ExecutionWithUserAndTask(
                        execution = it,
                        user = getUserById(it.userId),
                        task = getTaskById(it.taskId),
                    )
                }
                emit(executionsWithUserAndTask)
            }
    }
}
