package com.braiso22.turnos.users.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.braiso22.turnos.users.domain.User

@Entity
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val username: String,
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        username = username,
    )
}

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    username = username
)