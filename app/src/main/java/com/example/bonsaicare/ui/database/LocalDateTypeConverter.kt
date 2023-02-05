package com.example.bonsaicare.ui.database

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateTypeConverter {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): Long {
        // Convert the LocalDate object into a long
        return date.toEpochDay()
    }

    @TypeConverter
    fun toLocalDate(epochDay: Long): LocalDate {
        // Convert the long back into a LocalDate object
        return LocalDate.ofEpochDay(epochDay)
    }
}
