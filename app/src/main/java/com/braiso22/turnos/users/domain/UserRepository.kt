package com.braiso22.turnos.users.domain

import com.braiso22.turnos.common.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun emailSignIn(email: String, password: String): Flow<Resource<Unit>>
    fun emailLogin(email: String, password: String): Flow<Resource<Unit>>
    fun registerUser(email: String, username: String): Flow<Resource<Unit>>
    suspend fun getUserById(id: String): User?
}