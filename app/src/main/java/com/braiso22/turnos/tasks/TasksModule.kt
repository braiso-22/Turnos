package com.braiso22.turnos.tasks

import com.braiso22.turnos.di.AppDB
import com.braiso22.turnos.tasks.data.TaskRepositoryImpl
import com.braiso22.turnos.tasks.data.local.TaskDao
import com.braiso22.turnos.tasks.domain.GetTaskById
import com.braiso22.turnos.tasks.domain.GetTasks
import com.braiso22.turnos.tasks.domain.SyncTasks
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
    fun provideTaskDao(appDatabase: AppDB): TaskDao {
        return appDatabase.taskDao()
    }

    @Provides
    @Singleton
    fun provideTasksRepository(
        firebaseDB: FirebaseFirestore,
        taskDao: TaskDao,
    ): TasksRepository = TaskRepositoryImpl(
        firebaseDB,
        taskDao,
    )

    @Provides
    @Singleton
    fun provideGetTaskById(
        tasksRepository: TasksRepository,
    ): GetTaskById = GetTaskById(tasksRepository)

    @Provides
    @Singleton
    fun provideSyncTasks(
        tasksRepository: TasksRepository,
    ): SyncTasks = SyncTasks(tasksRepository)

    @Provides
    @Singleton
    fun provideGetTasks(
        tasksRepository: TasksRepository,
    ): GetTasks = GetTasks(tasksRepository)
}
