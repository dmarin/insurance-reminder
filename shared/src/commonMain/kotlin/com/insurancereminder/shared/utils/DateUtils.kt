package com.insurancereminder.shared.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.daysUntil

object DateUtils {
    fun getCurrentDate(): LocalDate {
        return Clock.System.todayIn(TimeZone.currentSystemDefault())
    }

    fun daysUntilExpiry(expiryDate: LocalDate): Int {
        val today = getCurrentDate()
        return today.daysUntil(expiryDate)
    }

    fun isExpiringSoon(expiryDate: LocalDate, reminderDaysBefore: Int): Boolean {
        val daysUntil = daysUntilExpiry(expiryDate)
        return daysUntil <= reminderDaysBefore && daysUntil >= 0
    }

    fun isExpired(expiryDate: LocalDate): Boolean {
        return daysUntilExpiry(expiryDate) < 0
    }

    fun formatDateForDisplay(date: LocalDate): String {
        return "${date.monthNumber}/${date.dayOfMonth}/${date.year}"
    }
}