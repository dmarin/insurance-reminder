package com.insurancereminder.shared.domain.usecase

import com.insurancereminder.shared.model.User
import com.insurancereminder.shared.model.SubscriptionTier
import com.insurancereminder.shared.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import dev.gitlive.firebase.auth.FirebaseUser

class AuthUseCases(
    private val authRepository: AuthRepository
) {

    val currentUser: Flow<FirebaseUser?> = authRepository.currentUser

    suspend fun signUp(email: String, password: String): Result<User> {
        return authRepository.signUp(email, password)
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return authRepository.signIn(email, password)
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            authRepository.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()
    }

    suspend fun upgradeToPremium(userId: String): Result<Unit> {
        return try {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                val upgradedUser = currentUser.copy(subscriptionTier = SubscriptionTier.PREMIUM)
                authRepository.updateUserSubscription(userId, upgradedUser)
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun shareWithPartner(userId: String, partnerEmail: String): Result<Boolean> {
        return authRepository.shareWithPartner(userId, partnerEmail)
    }

    fun validateEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.length > 5
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }
}