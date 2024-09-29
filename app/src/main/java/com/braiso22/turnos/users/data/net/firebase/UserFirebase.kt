package com.braiso22.turnos.users.data.net.firebase

import com.braiso22.turnos.users.domain.User

data class UserFirebase(
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