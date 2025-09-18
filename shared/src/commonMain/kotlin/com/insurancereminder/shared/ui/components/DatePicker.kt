package com.insurancereminder.shared.ui.components

import kotlinx.datetime.LocalDate

// Simple utility functions for date handling across platforms
object DatePickerUtils {
    fun formatDate(date: LocalDate): String = date.toString()

    fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun isValidDateFormat(dateString: String): Boolean {
        return parseDate(dateString) != null
    }
}