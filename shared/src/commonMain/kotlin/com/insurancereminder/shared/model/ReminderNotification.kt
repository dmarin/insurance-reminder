package com.insurancereminder.shared.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ReminderNotification(
    val id: String = "",
    val insuranceId: String,
    val insuranceName: String,
    val insuranceType: InsuranceType,
    val expiryDate: LocalDate,
    val reminderDate: LocalDate,
    val isShown: Boolean = false,
    val createdAt: LocalDateTime? = null
)