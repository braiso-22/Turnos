package com.braiso22.turnos.users

import android.content.Context
import androidx.room.Room
import com.braiso22.turnos.users.data.UserRepositoryImpl
import com.braiso22.turnos.users.data.local.room.UserDB
import com.braiso22.turnos.users.data.local.room.UserDao
import com.braiso22.turnos.users.data.net.api.AuthApi
import com.braiso22.turnos.users.data.net.api.UserApi
import com.braiso22.turnos.users.data.net.firebase.AuthApiFirebase
import com.braiso22.turnos.users.data.net.firebase.UserApiFirebase
import com.braiso22.turnos.users.domain.CreateUser
import com.braiso22.turnos.users.domain.GetCurrentUser
import com.braiso22.turnos.users.domain.GetUserById
import com.braiso22.turnos.users.domain.SyncUsers
import com.braiso22.turnos.users.domain.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UsersModule {

    @Provides
    @Singleton
    fun provideUserDatabase(
        @ApplicationContext appContext: Context,
    ): UserDB = Room.databaseBuilder(
        appContext,
        UserDB::class.java,
        "users"
    ).build()

    @Provides
    @Singleton
    fun provideUserDao(db: UserDB): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideAuthApi(auth: FirebaseAuth): AuthApi = AuthApiFirebase(auth)

    @Provides
    @Singleton
    fun provideUserApi(
        firestore: FirebaseFirestore,
    ): UserApi = UserApiFirebase(firestore = firestore)

    @Provides
    @Singleton
    fun provideUserRepository(
        authApi: AuthApi,
        userApi: UserApi,
        userDao: UserDao,
    ): UserRepository = UserRepositoryImpl(
        authApi = authApi,
        userApi = userApi,
        userDao = userDao
    )

    @Provides
    @Singleton
    fun provideCreateUserUseCase(
        userRepository: UserRepository,
    ): CreateUser = CreateUser(
        userRepository = userRepository
    )

    @Provides
    @Singleton
    fun provideSyncUsersUseCase(
        userRepository: UserRepository,
    ): SyncUsers = SyncUsers(
        userRepository = userRepository
    )

    @Provides
    @Singleton
    fun provideGetUserByIdUseCase(
        userRepository: UserRepository,
    ): GetUserById = GetUserById(
        userRepository = userRepository
    )

    @Provides
    @Singleton
    fun provideGetCurrentUser(
        userRepository: UserRepository,
    ): GetCurrentUser = GetCurrentUser(
        userRepository = userRepository
    )
}
