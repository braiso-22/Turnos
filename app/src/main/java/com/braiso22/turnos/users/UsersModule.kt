package com.braiso22.turnos.users

import com.braiso22.turnos.users.data.UserRepositoryImpl
import com.braiso22.turnos.users.domain.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UsersModule {
    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
    ): UserRepository = UserRepositoryImpl(
        auth = firebaseAuth,
        firestore = firebaseFirestore
    )
}
