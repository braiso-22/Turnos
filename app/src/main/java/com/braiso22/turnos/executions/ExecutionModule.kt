package com.braiso22.turnos.executions

import android.content.Context
import com.braiso22.turnos.di.AppDB
import com.braiso22.turnos.executions.data.ExecutionsRepositoryImpl
import com.braiso22.turnos.executions.data.FirebaseApi
import com.braiso22.turnos.executions.data.FirebaseApiImpl
import com.braiso22.turnos.executions.data.local.ExecutionDao
import com.braiso22.turnos.executions.domain.ExecutionsRepository
import com.braiso22.turnos.executions.domain.use_cases.ConfirmExecution
import com.braiso22.turnos.executions.domain.use_cases.GetExecutionsByTaskId
import com.braiso22.turnos.executions.domain.use_cases.GetOpenExecutions
import com.braiso22.turnos.executions.domain.use_cases.SaveExecution
import com.braiso22.turnos.executions.domain.use_cases.SyncExecutions
import com.braiso22.turnos.tasks.domain.GetTaskById
import com.braiso22.turnos.users.domain.GetCurrentUser
import com.braiso22.turnos.users.domain.GetUserById
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
object ExecutionModule {

    @Provides
    @Singleton
    fun provideExecutionDao(
        db: AppDB,
    ): ExecutionDao = db.executionDao()

    @Provides
    @Singleton
    fun provideExecutionsRepository(
        firebaseDB: FirebaseFirestore,
        firebaseApi: FirebaseApi,
        auth: FirebaseAuth,
        executionDao: ExecutionDao,
        @ApplicationContext context: Context,
    ): ExecutionsRepository = ExecutionsRepositoryImpl(
        firebaseDB,
        firebaseApi,
        auth,
        context,
        executionDao = executionDao,
    )

    @Provides
    @Singleton
    fun provideFirebaseApi(
        firestore: FirebaseFirestore,
    ): FirebaseApi = FirebaseApiImpl(
        firestore,
    )

    @Provides
    @Singleton
    fun provideGetExecutionsByTaskId(
        executionsRepository: ExecutionsRepository,
        getUserById: GetUserById,
    ): GetExecutionsByTaskId = GetExecutionsByTaskId(
        executionsRepository,
        getUserById,
    )

    @Provides
    @Singleton
    fun provideSaveExecution(
        executionsRepository: ExecutionsRepository,
    ): SaveExecution = SaveExecution(
        executionsRepository,
    )

    @Provides
    @Singleton
    fun provideSyncExecutions(
        executionsRepository: ExecutionsRepository,
    ): SyncExecutions = SyncExecutions(
        executionsRepository
    )

    @Provides
    @Singleton
    fun provideGetOpenExecutions(
        executionsRepository: ExecutionsRepository,
        getCurrentUser: GetCurrentUser,
        getUserById: GetUserById,
        getTaskById: GetTaskById,
    ): GetOpenExecutions = GetOpenExecutions(
        executionsRepository,
        getCurrentUser,
        getUserById,
        getTaskById,
    )

    @Provides
    @Singleton
    fun provideConfirmExecution(
        executionsRepository: ExecutionsRepository,
    ): ConfirmExecution = ConfirmExecution(
        executionsRepository,
    )
}
