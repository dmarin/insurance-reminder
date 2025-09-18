package com.insurancereminder.shared.theme

import com.insurancereminder.shared.model.InsuranceType

/**
 * Shared theme definitions for insurance types.
 * Instead of hardcoding colors, we define semantic roles that can be mapped
 * to platform-specific theme colors.
 */
enum class InsuranceThemeRole {
    PRIMARY,      // Main brand color
    SECONDARY,    // Secondary brand color
    SUCCESS,      // Success/positive states
    ERROR,        // Error/critical states
    WARNING,      // Warning states
    INFO,         // Informational states
    SURFACE,      // Surface backgrounds
    OUTLINE       // Borders and outlines
}

/**
 * Maps insurance types to semantic theme roles instead of hardcoded colors.
 * This allows each platform to interpret these roles according to their design system.
 */
object InsuranceThemeMapping {

    fun getThemeRole(type: InsuranceType): InsuranceThemeRole {
        return when (type) {
            InsuranceType.AUTO -> InsuranceThemeRole.PRIMARY
            InsuranceType.MOTORCYCLE -> InsuranceThemeRole.WARNING
            InsuranceType.HOME -> InsuranceThemeRole.SUCCESS
            InsuranceType.HEALTH -> InsuranceThemeRole.ERROR
            InsuranceType.DENTAL -> InsuranceThemeRole.SECONDARY
            InsuranceType.LIFE -> InsuranceThemeRole.PRIMARY
            InsuranceType.PET -> InsuranceThemeRole.SUCCESS
            InsuranceType.TRAVEL -> InsuranceThemeRole.INFO
            InsuranceType.OTHER -> InsuranceThemeRole.OUTLINE
        }
    }

    fun getIconName(type: InsuranceType): String {
        return when (type) {
            InsuranceType.AUTO -> "directions_car"
            InsuranceType.MOTORCYCLE -> "two_wheeler"
            InsuranceType.HOME -> "home"
            InsuranceType.HEALTH -> "local_hospital"
            InsuranceType.DENTAL -> "dentistry"
            InsuranceType.LIFE -> "favorite"
            InsuranceType.PET -> "pets"
            InsuranceType.TRAVEL -> "flight"
            InsuranceType.OTHER -> "category"
        }
    }

    fun getIconEmoji(type: InsuranceType): String {
        return when (type) {
            InsuranceType.AUTO -> "🚗"
            InsuranceType.MOTORCYCLE -> "🏍️"
            InsuranceType.HOME -> "🏠"
            InsuranceType.HEALTH -> "🏥"
            InsuranceType.DENTAL -> "🦷"
            InsuranceType.LIFE -> "❤️"
            InsuranceType.PET -> "🐕"
            InsuranceType.TRAVEL -> "✈️"
            InsuranceType.OTHER -> "📄"
        }
    }
}

/**
 * Status theme mappings for insurance states
 */
enum class InsuranceStatusTheme {
    ACTIVE,
    EXPIRING_SOON,
    EXPIRED
}

fun getStatusThemeRole(status: InsuranceStatusTheme): InsuranceThemeRole {
    return when (status) {
        InsuranceStatusTheme.ACTIVE -> InsuranceThemeRole.SUCCESS
        InsuranceStatusTheme.EXPIRING_SOON -> InsuranceThemeRole.WARNING
        InsuranceStatusTheme.EXPIRED -> InsuranceThemeRole.ERROR
    }
}