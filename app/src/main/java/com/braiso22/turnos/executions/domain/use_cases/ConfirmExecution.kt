package com.braiso22.turnos.executions.domain.use_cases

import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.executions.domain.ExecutionsRepository
import kotlinx.coroutines.flow.Flow

class ConfirmExecution(
    private val executionsRepository: ExecutionsRepository,
) {
    operator fun invoke(executionId: String): Flow<Resource<Unit>> {
        return executionsRepository.confirmExecution(executionId)
    }
}