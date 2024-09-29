package com.braiso22.turnos.users.data.net.api

import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.users.domain.User
import kotlinx.coroutines.flow.Flow

interface UserApi {
    fun registerUser(email: String, username: String): Flow<Resource<Unit>>
    fun listenOnlineUsers(): Flow<List<User>>
}