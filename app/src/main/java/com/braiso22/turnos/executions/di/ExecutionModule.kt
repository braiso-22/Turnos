package com.braiso22.turnos.executions.di

import android.content.Context
import com.braiso22.turnos.executions.data.ExecutionsRepositoryImpl
import com.braiso22.turnos.executions.data.FirebaseApi
import com.braiso22.turnos.executions.data.FirebaseApiImpl
import com.braiso22.turnos.executions.domain.ExecutionsRepository
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
    fun provideExecutionsRepository(
        firebaseDB: FirebaseFirestore,
        firebaseApi: FirebaseApi,
        auth: FirebaseAuth,
        @ApplicationContext context: Context,
    ): ExecutionsRepository = ExecutionsRepositoryImpl(
        firebaseDB,
        firebaseApi,
        auth,
        context,
    )

    @Provides
    @Singleton
    fun provideFirebaseApi(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): FirebaseApi = FirebaseApiImpl(
        auth,
        firestore,
    )
}