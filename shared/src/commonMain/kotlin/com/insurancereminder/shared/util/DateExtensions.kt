package com.insurancereminder.shared.util

import kotlinx.datetime.*

object DateUtils {
    fun isExpired(expiryDate: LocalDate): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return expiryDate < today
    }

    fun isExpiringSoon(expiryDate: LocalDate, daysThreshold: Int = 30): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val daysUntilExpiry = expiryDate.toEpochDays() - today.toEpochDays()
        return daysUntilExpiry in 0..daysThreshold
    }

    fun getDaysUntilExpiry(expiryDate: LocalDate): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return (expiryDate.toEpochDays() - today.toEpochDays()).toInt()
    }

    fun formatDateString(date: LocalDate): String {
        return date.toString() // In a real app, you'd format this based on locale
    }

    fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}

// Extension properties for Insurance model
val com.insurancereminder.shared.model.Insurance.isExpired: Boolean
    get() = DateUtils.isExpired(expiryDate)

val com.insurancereminder.shared.model.Insurance.isExpiringSoon: Boolean
    get() = DateUtils.isExpiringSoon(expiryDate)

val com.insurancereminder.shared.model.Insurance.daysUntilExpiry: Int
    get() = DateUtils.getDaysUntilExpiry(expiryDate)

val com.insurancereminder.shared.model.Insurance.formattedExpiryDate: String
    get() = DateUtils.formatDateString(expiryDate)