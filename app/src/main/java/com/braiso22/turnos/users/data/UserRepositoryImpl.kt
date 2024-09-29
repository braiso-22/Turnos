package com.braiso22.turnos.users.data

import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.users.data.local.room.UserDao
import com.braiso22.turnos.users.data.local.room.toEntity
import com.braiso22.turnos.users.data.net.api.AuthApi
import com.braiso22.turnos.users.data.net.api.UserApi
import com.braiso22.turnos.users.domain.User
import com.braiso22.turnos.users.domain.UserRepository
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val userDao: UserDao,
) : UserRepository {
    override fun emailSignIn(
        email: String,
        password: String,
    ): Flow<Resource<Unit>> = authApi.signIn(email, password)

    override fun emailLogin(
        email: String,
        password: String,
    ): Flow<Resource<Unit>> = authApi.login(email, password)

    override fun registerUser(
        email: String,
        username: String,
    ): Flow<Resource<Unit>> = userApi.registerUser(email, username)

    override suspend fun getCurrentUser(): User? {
        val email = getCurrentEmail() ?: ""
        return userDao.getUserByEmail(email)?.toDomain()
    }

    override suspend fun getCurrentEmail(): String? {
        return authApi.getCurrentUserEmail()
    }

    override fun listenOnlineUsers(): Flow<List<User>> = userApi.listenOnlineUsers()

    override suspend fun syncUsers(users: List<User>) {
        userDao.insertAll(
            users = users.map {
                it.toEntity()
            }
        )
    }

    override suspend fun getUserById(id: String): User? {
        return userDao.getUserById(id)?.toDomain()
    }
}