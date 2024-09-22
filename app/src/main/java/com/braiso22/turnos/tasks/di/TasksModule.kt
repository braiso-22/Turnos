package com.braiso22.turnos.tasks.di

import com.braiso22.turnos.tasks.data.TaskRepositoryImpl
import com.braiso22.turnos.tasks.domain.TasksRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TasksModule {

    @Provides
    @Singleton
    fun provideTasksRepository(
        firebaseDB: FirebaseFirestore,
    ): TasksRepository = TaskRepositoryImpl(firebaseDB)
}
