package com.braiso22.turnos.executions.di

import com.braiso22.turnos.executions.data.ExecutionsRepositoryImpl
import com.braiso22.turnos.executions.domain.ExecutionsRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExecutionModule {

    @Provides
    @Singleton
    fun provideExecutionsRepository(
        firebaseDB: FirebaseFirestore,
    ): ExecutionsRepository = ExecutionsRepositoryImpl(firebaseDB)
}