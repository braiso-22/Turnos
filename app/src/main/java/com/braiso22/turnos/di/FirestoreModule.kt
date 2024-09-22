package com.braiso22.turnos.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {
    @Provides
    @Singleton
    fun provideFirestoreDb(): FirebaseFirestore = FirebaseFirestore.getInstance()
}