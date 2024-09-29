package com.braiso22.turnos.users.domain

data class User(
    val id: String,
    val username: String,
    val email: String,
    val imageUrl: String? = null,
)
