package com.braiso22.turnos.users.data

import com.braiso22.turnos.users.domain.User

data class UserDto(
    val id: String = "",
    val email: String = "",
    val username: String = "",
) {
    fun toDomain(): User = User(
        id,
        username,
        email
    )
}