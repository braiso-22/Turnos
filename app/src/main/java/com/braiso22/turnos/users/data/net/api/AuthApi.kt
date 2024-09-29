package com.braiso22.turnos.users.data.net.api

import com.braiso22.turnos.common.Resource
import kotlinx.coroutines.flow.Flow

interface AuthApi {
    fun signIn(email: String, password: String): Flow<Resource<Unit>>
    fun login(email: String, password: String): Flow<Resource<Unit>>
    suspend fun getCurrentUserEmail():String?
}