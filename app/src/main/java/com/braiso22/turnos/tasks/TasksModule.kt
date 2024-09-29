package com.braiso22.turnos.tasks

import android.content.Context
import androidx.room.Room
import com.braiso22.turnos.tasks.data.TaskRepositoryImpl
import com.braiso22.turnos.tasks.data.local.TaskDB
import com.braiso22.turnos.tasks.data.local.TaskDao
import com.braiso22.turnos.tasks.domain.GetTaskById
import com.braiso22.turnos.tasks.domain.GetTasks
import com.braiso22.turnos.tasks.domain.SyncTasks
import com.braiso22.turnos.tasks.domain.TasksRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TasksModule {

    @Provides
    @Singleton
    fun provideTaskDB(@ApplicationContext context: Context): TaskDB {
        return Room.databaseBuilder(
            context = context,
            TaskDB::class.java,
            "tasks"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(appDatabase: TaskDB): TaskDao {
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
