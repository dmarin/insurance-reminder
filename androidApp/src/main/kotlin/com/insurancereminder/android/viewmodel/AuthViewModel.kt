package com.insurancereminder.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insurancereminder.shared.di.SharedModule
import com.insurancereminder.shared.presentation.AuthState
import com.insurancereminder.shared.presentation.LoginFormState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    private val sharedModule: SharedModule = SharedModule()
) : ViewModel() {

    private val authUseCases = sharedModule.authUseCases

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginFormState = MutableStateFlow(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginFormState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authUseCases.currentUser.collect { firebaseUser ->
                if (firebaseUser != null) {
                    val user = authUseCases.getCurrentUser()
                    _authState.value = _authState.value.copy(
                        currentUser = user,
                        isAuthenticated = true,
                        isGuestMode = false,
                        isLoading = false
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        currentUser = null,
                        isAuthenticated = false,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateEmail(email: String) {
        val isValid = authUseCases.validateEmail(email)
        _loginFormState.value = _loginFormState.value.copy(
            email = email,
            isEmailValid = isValid
        )
    }

    fun updatePassword(password: String) {
        val isValid = authUseCases.validatePassword(password)
        val passwordsMatch = password == _loginFormState.value.confirmPassword || _loginFormState.value.confirmPassword.isEmpty()

        _loginFormState.value = _loginFormState.value.copy(
            password = password,
            isPasswordValid = isValid,
            passwordsMatch = passwordsMatch
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        val passwordsMatch = confirmPassword == _loginFormState.value.password
        _loginFormState.value = _loginFormState.value.copy(
            confirmPassword = confirmPassword,
            passwordsMatch = passwordsMatch
        )
    }

    fun togglePasswordVisibility() {
        _loginFormState.value = _loginFormState.value.copy(
            showPassword = !_loginFormState.value.showPassword
        )
    }

    fun switchToSignUp() {
        _loginFormState.value = _loginFormState.value.copy(isSignUpMode = true)
    }

    fun switchToSignIn() {
        _loginFormState.value = _loginFormState.value.copy(isSignUpMode = false)
    }

    fun signUp() {
        val formState = _loginFormState.value
        if (!formState.isValidForSignUp) return

        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authUseCases.signUp(formState.email, formState.password)

            _authState.value = _authState.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun signIn() {
        val formState = _loginFormState.value
        if (!formState.isValidForSignIn) return

        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authUseCases.signIn(formState.email, formState.password)

            _authState.value = _authState.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true)

            val result = authUseCases.signOut()

            _authState.value = _authState.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun useAsGuest() {
        _authState.value = _authState.value.copy(
            isGuestMode = true,
            isAuthenticated = false
        )
    }

    fun upgradeToPremium() {
        viewModelScope.launch {
            val currentUser = _authState.value.currentUser
            if (currentUser != null) {
                _authState.value = _authState.value.copy(isLoading = true)

                val result = authUseCases.upgradeToPremium(currentUser.id)

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun shareWithPartner(partnerEmail: String) {
        viewModelScope.launch {
            val currentUser = _authState.value.currentUser
            if (currentUser != null && _authState.value.canAccessPremiumFeatures) {
                _authState.value = _authState.value.copy(isLoading = true)

                val result = authUseCases.shareWithPartner(currentUser.id, partnerEmail)

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: if (result.isFailure) "Failed to share with partner" else null
                )
            } else {
                _authState.value = _authState.value.copy(
                    error = "Premium subscription required"
                )
            }
        }
    }

    fun exitGuestMode() {
        _authState.value = _authState.value.copy(
            isGuestMode = false,
            isAuthenticated = false
        )
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}