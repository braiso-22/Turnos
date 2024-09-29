package com.braiso22.turnos.executions.domain.use_cases

import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.executions.domain.Execution
import com.braiso22.turnos.executions.domain.ExecutionsRepository
import kotlinx.coroutines.flow.Flow

class SaveExecution(
    private val executionsRepository: ExecutionsRepository,
) {
    operator fun invoke(execution: Execution): Flow<Resource<Execution>> {
        return executionsRepository.saveExecution(execution)
    }
}
