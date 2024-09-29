package com.braiso22.turnos.executions.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate
import java.time.LocalDateTime

@TypeConverters(DBConverters::class)
@Database(entities = [ExecutionEntity::class], version = 1)
abstract class ExecutionDB : RoomDatabase() {
    abstract fun executionDao(): ExecutionDao
}

class DBConverters {
    // nullable
    @TypeConverter
    fun fromTimestampToLocalDate(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(it)
        }
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    // not null
    @TypeConverter
    fun fromTimestampString(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
    }

    @TypeConverter
    fun dateToTimestampString(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }
}