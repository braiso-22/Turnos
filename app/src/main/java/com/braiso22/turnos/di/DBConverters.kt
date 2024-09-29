package com.braiso22.turnos.di

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

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