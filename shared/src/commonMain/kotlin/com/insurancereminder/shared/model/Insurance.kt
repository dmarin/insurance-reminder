package com.insurancereminder.shared.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Insurance(
    val id: String = "",
    val type: InsuranceType,
    val name: String,
    val expiryDate: LocalDate,
    val reminderDaysBefore: Int = 30,
    val isActive: Boolean = true,
    val currentPrice: Double? = null,
    val currency: String = "EUR",
    val policyFileUrl: String? = null,
    val policyFileName: String? = null,
    val companyName: String? = null,
    val companyId: String? = null,
    val companyLogoUrl: String? = null,
    val policyNumber: String? = null,
    val userId: String? = null,
    val sharedWithUserId: String? = null,
    val createdAt: LocalDate? = null,
    val updatedAt: LocalDate? = null
)

@Serializable
enum class InsuranceType(val displayName: String, val iconName: String, val category: String) {
    AUTO("Auto Insurance", "directions_car", "Vehicle"),
    MOTORCYCLE("Motorcycle Insurance", "two_wheeler", "Vehicle"),
    HOME("Home Insurance", "home", "Property"),
    HEALTH("Health Insurance", "local_hospital", "Health"),
    DENTAL("Dental Insurance", "dentistry", "Health"),
    LIFE("Life Insurance", "favorite", "Life & Family"),
    PET("Pet Insurance", "pets", "Life & Family"),
    TRAVEL("Travel Insurance", "flight", "Travel"),
    OTHER("Other", "category", "Other")
}