package com.braiso22.turnos.users.domain

class GetUserById(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(id: String): User? {
        return userRepository.getUserById(id)
    }
}
