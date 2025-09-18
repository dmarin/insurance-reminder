package com.insurancereminder.shared.domain.usecase

import com.insurancereminder.shared.model.Insurance
import com.insurancereminder.shared.model.InsuranceType
import com.insurancereminder.shared.repository.InsuranceRepository
import com.insurancereminder.shared.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class InsuranceUseCases(
    private val repository: InsuranceRepository,
    private val authRepository: AuthRepository
) {

    fun getActiveInsurances(): Flow<List<Insurance>> {
        return repository.getActiveInsurances()
    }

    suspend fun getActiveInsurancesForCurrentUser(): Flow<List<Insurance>> {
        return try {
            println("InsuranceUseCases: Getting current user...")
            val currentUser = authRepository.currentUser.first()
            val userId = currentUser?.uid
            println("InsuranceUseCases: Current user ID: $userId")
            println("InsuranceUseCases: Current user email: ${currentUser?.email}")

            if (userId != null) {
                println("InsuranceUseCases: Querying for user $userId")
                val flow = repository.getActiveInsurancesForUser(userId)
                println("InsuranceUseCases: Got Flow from repository")
                flow
            } else {
                println("InsuranceUseCases: No user ID, using general query")
                repository.getActiveInsurances() // Fallback for guest mode
            }
        } catch (e: Exception) {
            println("InsuranceUseCases: Error getting user: ${e.message}")
            e.printStackTrace()
            repository.getActiveInsurances() // Fallback
        }
    }

    suspend fun addInsurance(
        name: String,
        type: InsuranceType,
        expiryDate: LocalDate,
        reminderDaysBefore: Int = 30,
        currentPrice: Double? = null,
        companyName: String? = null,
        companyId: String? = null,
        companyLogoUrl: String? = null,
        policyNumber: String? = null
    ): Result<String> {
        return try {
            val now = Clock.System.todayIn(TimeZone.currentSystemDefault())

            // Get current user ID
            val currentUser = authRepository.currentUser.first()
            val userId = currentUser?.uid

            val insurance = Insurance(
                name = name,
                type = type,
                expiryDate = expiryDate,
                reminderDaysBefore = reminderDaysBefore,
                currentPrice = currentPrice,
                companyName = companyName,
                companyId = companyId,
                companyLogoUrl = companyLogoUrl,
                policyNumber = policyNumber,
                userId = userId, // Set the user ID for the insurance
                createdAt = now,
                updatedAt = now
            )
            println("InsuranceUseCases: Adding insurance for user $userId: $insurance")
            val id = repository.addInsurance(insurance)
            println("InsuranceUseCases: Insurance added with ID: $id")
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateInsurance(insurance: Insurance): Result<Unit> {
        return try {
            val now = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val updatedInsurance = insurance.copy(updatedAt = now)
            repository.updateInsurance(updatedInsurance)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteInsurance(insuranceId: String): Result<Unit> {
        return try {
            repository.deleteInsurance(insuranceId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renewInsurance(insuranceId: String, newExpiryDate: LocalDate): Result<Unit> {
        return try {
            // Get the existing insurance
            val allInsurances = mutableListOf<Insurance>()
            repository.getActiveInsurances().collect { list ->
                allInsurances.addAll(list)
            }

            val existingInsurance = allInsurances.find { it.id == insuranceId }
                ?: return Result.failure(Exception("Insurance not found"))

            // Update with new expiry date
            val renewedInsurance = existingInsurance.copy(
                expiryDate = newExpiryDate,
                updatedAt = Clock.System.todayIn(TimeZone.currentSystemDefault())
            )

            repository.updateInsurance(renewedInsurance)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportInsurances(userId: String): Result<List<Insurance>> {
        return try {
            // For now, just return active insurances - this can be enhanced later
            val insurances = mutableListOf<Insurance>()
            repository.getActiveInsurances().collect { list ->
                insurances.addAll(list)
            }
            Result.success(insurances)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}