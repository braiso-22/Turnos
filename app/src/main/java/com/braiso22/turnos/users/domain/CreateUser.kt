package com.braiso22.turnos.users.domain

import com.braiso22.turnos.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateUser(
    private val userRepository: UserRepository,
) {
    operator fun invoke(
        username: String,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val email = userRepository.getCurrentEmail()

        if (email == null) {
            emit(Resource.Error(Exception("User not logged in")))
            return@flow
        }

        userRepository.registerUser(email, username).collect {
            emit(it)
        }
    }
}