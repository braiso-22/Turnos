package com.braiso22.turnos.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppDBModule {
    @Provides
    @Singleton
    fun provideTaskDB(@ApplicationContext context: Context): AppDB {
        return Room.databaseBuilder(
            context = context,
            AppDB::class.java,
            "app_db"
        ).build()
    }
}