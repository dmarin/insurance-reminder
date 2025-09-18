package com.insurancereminder.shared.constants

object AppConstants {
    // Subscription limits
    const val FREE_TIER_MAX_POLICIES = 5
    const val PREMIUM_TIER_MAX_POLICIES = Int.MAX_VALUE

    // Default values
    const val DEFAULT_REMINDER_DAYS = 30
    const val DEFAULT_CURRENCY = "EUR"

    // File upload
    const val MAX_FILE_SIZE_MB = 10
    const val ALLOWED_FILE_TYPES = "application/pdf"

    // Expiry thresholds
    const val EXPIRING_SOON_DAYS = 30
    const val CRITICAL_EXPIRING_DAYS = 7

    // Form validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_INSURANCE_NAME_LENGTH = 3
    const val MAX_INSURANCE_NAME_LENGTH = 100

    // Notification intervals
    const val NOTIFICATION_CHECK_INTERVAL_HOURS = 24
    const val NOTIFICATION_REMINDER_HOUR = 9 // 9 AM

    // Premium features
    val PREMIUM_FEATURES = listOf(
        "Unlimited insurance policies",
        "Cloud storage and sync",
        "PDF file uploads",
        "Partner sharing",
        "Advanced notifications",
        "Cross-device sync",
        "Data backup and export"
    )

    // Free features
    val FREE_FEATURES = listOf(
        "Up to $FREE_TIER_MAX_POLICIES insurance policies",
        "Local storage only",
        "Basic notifications",
        "No file uploads",
        "No sharing features"
    )
}

object ErrorMessages {
    const val NETWORK_ERROR = "Network error. Please check your connection."
    const val AUTHENTICATION_ERROR = "Authentication failed. Please try again."
    const val PERMISSION_DENIED = "Permission denied. Please check your access rights."
    const val FILE_TOO_LARGE = "File size exceeds ${AppConstants.MAX_FILE_SIZE_MB}MB limit."
    const val INVALID_FILE_TYPE = "Only PDF files are allowed."
    const val PREMIUM_REQUIRED = "Premium subscription required for this feature."
    const val MAX_POLICIES_REACHED = "Maximum number of policies reached for free tier."
}

object ValidationMessages {
    const val EMAIL_REQUIRED = "Email is required"
    const val EMAIL_INVALID = "Invalid email format"
    const val PASSWORD_REQUIRED = "Password is required"
    const val PASSWORD_TOO_SHORT = "Password must be at least ${AppConstants.MIN_PASSWORD_LENGTH} characters"
    const val PASSWORDS_DONT_MATCH = "Passwords don't match"
    const val INSURANCE_NAME_REQUIRED = "Insurance name is required"
    const val INSURANCE_NAME_TOO_SHORT = "Name must be at least ${AppConstants.MIN_INSURANCE_NAME_LENGTH} characters"
    const val EXPIRY_DATE_REQUIRED = "Expiry date is required"
    const val EXPIRY_DATE_INVALID = "Invalid date format (YYYY-MM-DD)"
}