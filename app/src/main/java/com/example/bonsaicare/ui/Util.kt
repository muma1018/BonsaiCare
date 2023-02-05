package com.example.bonsaicare.ui

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Calculate age given a LocalDate
fun calculateAge(dateOfBirth: LocalDate): Int {
    val currentDate = LocalDate.now()
    val period = Period.between(dateOfBirth, currentDate)
    return period.years
}

// Convert date strings from "2022-2-1" to "2022-02-01"
// Returns false if conversion is not possible
fun convertDateStringToIsoFormat(date: String): String {
    val parts = date.split("-")
    val year = parts[0]
    val month = parts[1].padStart(2, '0')
    val day = parts[2].padStart(2, '0')
    return "$year-$month-$day"
}

// Check if a DateString is valid "yyyy-MM-dd" format
fun isValidDateString(dateString: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return try {
        formatter.parse(dateString)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}
