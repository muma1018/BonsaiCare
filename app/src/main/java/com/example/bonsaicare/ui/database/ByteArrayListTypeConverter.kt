package com.example.bonsaicare.ui.database

import androidx.room.TypeConverter

class ByteArrayListTypeConverter {
    @TypeConverter
    fun fromByteArrayList(list: List<ByteArray>): String {
        // Convert the list of ByteArray objects into a string
        return list.joinToString(separator = ",") { it.toString(Charsets.UTF_8) }
    }

    @TypeConverter
    fun toByteArrayList(string: String): List<ByteArray> {
        // Convert the string back into a list of ByteArray objects
        return string.split(",").map { it.toByteArray(Charsets.UTF_8) }
    }
}