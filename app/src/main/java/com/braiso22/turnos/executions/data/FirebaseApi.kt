package com.braiso22.turnos.executions.data

import com.braiso22.turnos.users.data.UserDto
import kotlinx.coroutines.flow.Flow

fun interface FirebaseApi {
    fun getUserByEmail(email: String): Flow<UserDto?>
}