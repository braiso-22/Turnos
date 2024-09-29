package com.braiso22.turnos.users.domain

class GetCurrentUser(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): User? {
        return userRepository.getCurrentUser()
    }
}