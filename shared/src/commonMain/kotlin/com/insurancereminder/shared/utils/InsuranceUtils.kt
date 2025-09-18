package com.insurancereminder.shared.utils

import com.insurancereminder.shared.model.Insurance
import com.insurancereminder.shared.model.InsuranceType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.until

object InsuranceUtils {

    fun getDaysUntilExpiry(expiryDate: LocalDate): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return today.until(expiryDate, kotlinx.datetime.DateTimeUnit.DAY)
    }

    fun isExpiringSoon(expiryDate: LocalDate, reminderDaysBefore: Int): Boolean {
        val daysUntilExpiry = getDaysUntilExpiry(expiryDate)
        return daysUntilExpiry in 1..reminderDaysBefore
    }

    fun isExpired(expiryDate: LocalDate): Boolean {
        return getDaysUntilExpiry(expiryDate) < 0
    }

    fun getInsuranceStatus(insurance: Insurance): InsuranceStatus {
        val daysUntilExpiry = getDaysUntilExpiry(insurance.expiryDate)
        return when {
            daysUntilExpiry < 0 -> InsuranceStatus.EXPIRED
            daysUntilExpiry <= insurance.reminderDaysBefore -> InsuranceStatus.EXPIRING_SOON
            else -> InsuranceStatus.ACTIVE
        }
    }

    fun getInsuranceTypeIcon(type: InsuranceType): String {
        return when (type) {
            InsuranceType.AUTO -> "directions_car"
            InsuranceType.MOTORCYCLE -> "directions_bike"
            InsuranceType.HOME -> "home"
            InsuranceType.HEALTH -> "local_hospital"
            InsuranceType.DENTAL -> "local_hospital"
            InsuranceType.LIFE -> "favorite"
            InsuranceType.PET -> "pets"
            InsuranceType.TRAVEL -> "flight"
            InsuranceType.OTHER -> "category"
        }
    }

    fun getInsuranceTypeColor(type: InsuranceType): InsuranceTypeColor {
        return when (type) {
            InsuranceType.AUTO -> InsuranceTypeColor(0xFF1976D2)
            InsuranceType.MOTORCYCLE -> InsuranceTypeColor(0xFFFF9800)
            InsuranceType.HOME -> InsuranceTypeColor(0xFF388E3C)
            InsuranceType.HEALTH -> InsuranceTypeColor(0xFFD32F2F)
            InsuranceType.DENTAL -> InsuranceTypeColor(0xFFE91E63)
            InsuranceType.LIFE -> InsuranceTypeColor(0xFF7B1FA2)
            InsuranceType.PET -> InsuranceTypeColor(0xFF8BC34A)
            InsuranceType.TRAVEL -> InsuranceTypeColor(0xFF0097A7)
            InsuranceType.OTHER -> InsuranceTypeColor(0xFF795548)
        }
    }

    fun getInsuranceTypeBackgroundImage(type: InsuranceType): String {
        return when (type) {
            InsuranceType.AUTO -> "https://images.unsplash.com/photo-1494905998402-395d579af36f?w=400"
            InsuranceType.MOTORCYCLE -> "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400"
            InsuranceType.HOME -> "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=400"
            InsuranceType.HEALTH -> "https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400"
            InsuranceType.DENTAL -> "https://images.unsplash.com/photo-1609840114035-3c981b782dfe?w=400"
            InsuranceType.LIFE -> "https://images.unsplash.com/photo-1511632765486-a01980e01a18?w=400"
            InsuranceType.PET -> "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=400"
            InsuranceType.TRAVEL -> "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=400"
            InsuranceType.OTHER -> "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=400"
        }
    }
}

enum class InsuranceStatus {
    ACTIVE,
    EXPIRING_SOON,
    EXPIRED
}

data class InsuranceTypeColor(val value: Long)

// Extension functions for Insurance
val Insurance.daysUntilExpiry: Int
    get() = InsuranceUtils.getDaysUntilExpiry(expiryDate)

val Insurance.isExpiringSoon: Boolean
    get() = InsuranceUtils.isExpiringSoon(expiryDate, reminderDaysBefore)

val Insurance.isExpired: Boolean
    get() = InsuranceUtils.isExpired(expiryDate)

val Insurance.status: InsuranceStatus
    get() = InsuranceUtils.getInsuranceStatus(this)