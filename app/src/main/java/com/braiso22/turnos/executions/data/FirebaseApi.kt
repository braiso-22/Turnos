package com.braiso22.turnos.executions.data

import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.users.data.net.firebase.UserFirebase
import kotlinx.coroutines.flow.Flow

interface FirebaseApi {
    fun getUserByEmail(email: String): Flow<UserFirebase?>
    fun confirmExecution(executionId: String): Flow<Resource<Unit>>
}