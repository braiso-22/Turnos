package com.braiso22.turnos.executions.domain.use_cases

import com.braiso22.turnos.executions.domain.ExecutionsRepository

class SyncExecutions(
    private val executionsRepository: ExecutionsRepository,
) {
    suspend operator fun invoke() {
        executionsRepository.listenOnlineExecutions().collect {
            executionsRepository.syncExecutions(it)
        }
    }
}