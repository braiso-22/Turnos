package com.braiso22.turnos.di

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.braiso22.turnos.executions.data.local.ExecutionDao
import com.braiso22.turnos.executions.data.local.ExecutionEntity
import com.braiso22.turnos.tasks.data.local.TaskDao
import com.braiso22.turnos.tasks.data.local.TaskEntity
import com.braiso22.turnos.users.data.local.room.UserDao
import com.braiso22.turnos.users.data.local.room.UserEntity

@Database(
    entities = [
        TaskEntity::class,
        UserEntity::class,
        ExecutionEntity::class
    ],
    version = 1,
)
@TypeConverters(DBConverters::class)
abstract class AppDB : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun executionDao(): ExecutionDao
}
