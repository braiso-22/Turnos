package com.braiso22.turnos.users.domain

class SyncUsers(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke() {
        userRepository.listenOnlineUsers().collect {
            userRepository.syncUsers(it)
        }
    }
}