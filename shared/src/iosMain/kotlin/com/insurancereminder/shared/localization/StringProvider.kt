package com.insurancereminder.shared.localization

actual class StringProvider {
    actual fun getString(key: String): String {
        // For now, return the key itself as fallback
        // In a real implementation, you'd load from iOS localization files
        return key
    }

    actual fun getString(key: String, vararg args: Any): String {
        // Simple string formatting for iOS
        val format = getString(key)
        return if (args.isEmpty()) {
            format
        } else {
            // For now, just return the format string
            // In a real implementation, you'd do proper string formatting
            format
        }
    }

    actual fun getCurrentLanguage(): String {
        return "en" // Default to English for now
    }
}