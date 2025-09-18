package com.insurancereminder.shared.presentation.components

object FormValidation {
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "Email is required")
            !email.contains("@") -> ValidationResult(false, "Invalid email format")
            !email.contains(".") -> ValidationResult(false, "Invalid email format")
            email.length < 5 -> ValidationResult(false, "Email too short")
            else -> ValidationResult(true, null)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, "Password is required")
            password.length < 6 -> ValidationResult(false, "Password must be at least 6 characters")
            else -> ValidationResult(true, null)
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult(false, "Confirm password is required")
            password != confirmPassword -> ValidationResult(false, "Passwords don't match")
            else -> ValidationResult(true, null)
        }
    }

    fun validateInsuranceName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Insurance name is required")
            name.length < 3 -> ValidationResult(false, "Name must be at least 3 characters")
            else -> ValidationResult(true, null)
        }
    }

    fun validateExpiryDate(dateString: String): ValidationResult {
        return when {
            dateString.isBlank() -> ValidationResult(false, "Expiry date is required")
            !isValidDateFormat(dateString) -> ValidationResult(false, "Invalid date format (YYYY-MM-DD)")
            else -> ValidationResult(true, null)
        }
    }

    private fun isValidDateFormat(dateString: String): Boolean {
        return try {
            // Simple regex validation for YYYY-MM-DD format
            val regex = Regex("\\d{4}-\\d{2}-\\d{2}")
            regex.matches(dateString)
        } catch (e: Exception) {
            false
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String?
)