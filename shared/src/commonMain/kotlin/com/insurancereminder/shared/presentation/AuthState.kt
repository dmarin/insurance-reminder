package com.insurancereminder.shared.presentation

import com.insurancereminder.shared.model.User

data class AuthState(
    val currentUser: User? = null,
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGuestMode: Boolean = true
) {
    // New monetization: Any registered user gets premium features, guests get limited features
    val canAccessPremiumFeatures: Boolean get() = isAuthenticated
    val shouldShowUpgradePrompt: Boolean get() = isGuestMode
    val userDisplayName: String get() = currentUser?.displayName ?: currentUser?.email ?: "Guest"
    val subscriptionDisplayName: String get() = if (isAuthenticated) "Premium" else "Free (Guest)"
}

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val passwordsMatch: Boolean = true,
    val showPassword: Boolean = false,
    val isSignUpMode: Boolean = false
) {
    val isValidForSignIn: Boolean get() = email.isNotBlank() && password.isNotBlank() && isEmailValid && isPasswordValid
    val isValidForSignUp: Boolean get() = isValidForSignIn && confirmPassword.isNotBlank() && passwordsMatch
}