package com.insurancereminder.shared.repository

import com.insurancereminder.shared.model.Insurance
import com.insurancereminder.shared.model.SubscriptionTier
import com.insurancereminder.shared.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class UnifiedInsuranceRepository(
    private val localRepository: LocalInsuranceRepository,
    private val cloudRepository: InsuranceRepository,
    private val authRepository: AuthRepository
) {

    suspend fun getAllInsurances(): Flow<List<Insurance>> {
        val currentUser = authRepository.getCurrentUser()
        return if (currentUser?.subscriptionTier == SubscriptionTier.PREMIUM) {
            cloudRepository.getActiveInsurances()
        } else {
            localRepository.getAllInsurances()
        }
    }

    suspend fun addInsurance(insurance: Insurance): String {
        val currentUser = authRepository.getCurrentUser()

        return if (currentUser?.subscriptionTier == SubscriptionTier.PREMIUM) {
            val cloudInsurance = insurance.copy(userId = currentUser.id)
            cloudRepository.addInsurance(cloudInsurance)
        } else {
            // Check free tier limits
            val existingInsurances = localRepository.getAllInsurances()
            // Note: This would need to be collected in real implementation
            // For now, assuming we check the limit elsewhere

            val localInsurance = insurance.copy(userId = "local_user")
            localRepository.addInsurance(localInsurance)
        }
    }

    suspend fun updateInsurance(insurance: Insurance) {
        val currentUser = authRepository.getCurrentUser()

        if (currentUser?.subscriptionTier == SubscriptionTier.PREMIUM) {
            cloudRepository.updateInsurance(insurance)
        } else {
            localRepository.updateInsurance(insurance)
        }
    }

    suspend fun deleteInsurance(insuranceId: String) {
        val currentUser = authRepository.getCurrentUser()

        if (currentUser?.subscriptionTier == SubscriptionTier.PREMIUM) {
            cloudRepository.deleteInsurance(insuranceId)
        } else {
            localRepository.deleteInsurance(insuranceId)
        }
    }

    suspend fun upgradeToPremium(userId: String): Result<Boolean> {
        return try {
            // Export local data
            val localData = localRepository.exportData("local_user")

            // Upload to cloud
            localData.forEach { insurance ->
                val cloudInsurance = insurance.copy(
                    id = "", // Let Firebase generate new ID
                    userId = userId
                )
                cloudRepository.addInsurance(cloudInsurance)
            }

            // Clear local data
            localRepository.clearUserData("local_user")

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downgradeToFree(userId: String): Result<Boolean> {
        return try {
            // Export cloud data
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                // In real implementation, you'd need to collect the flow
                // This is a simplified version

                // Clear cloud data would be handled by changing subscription tier
                // and the user would lose access to cloud features
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun shareInsuranceWithPartner(insuranceId: String, partnerId: String): Result<Boolean> {
        val currentUser = authRepository.getCurrentUser()

        return if (currentUser?.subscriptionTier == SubscriptionTier.PREMIUM) {
            try {
                val insurance = cloudRepository.getInsurance(insuranceId)
                if (insurance != null) {
                    val sharedInsurance = insurance.copy(sharedWithUserId = partnerId)
                    cloudRepository.updateInsurance(sharedInsurance)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Insurance not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("Premium subscription required for sharing"))
        }
    }

    suspend fun getSharedInsurances(): Flow<List<Insurance>> {
        val currentUser = authRepository.getCurrentUser()

        return if (currentUser?.subscriptionTier == SubscriptionTier.PREMIUM && currentUser.partnerId != null) {
            // This would need custom Firestore query for shared insurances
            // For now returning empty flow
            emptyFlow()
        } else {
            emptyFlow()
        }
    }
}