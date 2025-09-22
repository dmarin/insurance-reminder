package com.insurancereminder.shared.repository

import com.insurancereminder.shared.model.Insurance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Smart repository that automatically chooses between cloud and local storage
 * - When authenticated: Uses Firestore (cloud)
 * - When in guest mode: Uses local storage
 */
class SmartInsuranceRepository(
    private val cloudRepository: InsuranceRepository,
    private val localRepository: MockLocalInsuranceRepository,
    private val authRepository: AuthRepository
) : IInsuranceRepository {

    override suspend fun addInsurance(insurance: Insurance): String {
        return try {
            val currentUser = authRepository.currentUser.first()
            if (currentUser != null) {
                println("SmartInsuranceRepository: User authenticated, using cloud storage")
                cloudRepository.addInsurance(insurance)
            } else {
                println("SmartInsuranceRepository: Guest mode, using local storage")
                localRepository.addInsurance(insurance.copy(userId = "guest"))
            }
        } catch (e: Exception) {
            println("SmartInsuranceRepository: Cloud failed, falling back to local: ${e.message}")
            localRepository.addInsurance(insurance.copy(userId = "guest"))
        }
    }

    override suspend fun updateInsurance(insurance: Insurance) {
        try {
            val currentUser = authRepository.currentUser.first()
            if (currentUser != null) {
                cloudRepository.updateInsurance(insurance)
            } else {
                localRepository.updateInsurance(insurance)
            }
        } catch (e: Exception) {
            println("SmartInsuranceRepository: Cloud update failed, trying local: ${e.message}")
            localRepository.updateInsurance(insurance)
        }
    }

    override suspend fun deleteInsurance(insuranceId: String) {
        try {
            val currentUser = authRepository.currentUser.first()
            if (currentUser != null) {
                cloudRepository.deleteInsurance(insuranceId)
            } else {
                localRepository.deleteInsurance(insuranceId)
            }
        } catch (e: Exception) {
            println("SmartInsuranceRepository: Cloud delete failed, trying local: ${e.message}")
            localRepository.deleteInsurance(insuranceId)
        }
    }

    override suspend fun getInsurance(insuranceId: String): Insurance? {
        return try {
            val currentUser = authRepository.currentUser.first()
            if (currentUser != null) {
                cloudRepository.getInsurance(insuranceId)
            } else {
                localRepository.getInsurance(insuranceId)
            }
        } catch (e: Exception) {
            println("SmartInsuranceRepository: Cloud get failed, trying local: ${e.message}")
            localRepository.getInsurance(insuranceId)
        }
    }

    override fun getAllInsurances(): Flow<List<Insurance>> {
        return authRepository.currentUser.flatMapLatest { currentUser ->
            try {
                if (currentUser != null) {
                    println("SmartInsuranceRepository: User authenticated, getting from cloud")
                    cloudRepository.getAllInsurances()
                } else {
                    println("SmartInsuranceRepository: Guest mode, getting from local")
                    localRepository.getAllInsurances()
                }
            } catch (e: Exception) {
                println("SmartInsuranceRepository: Cloud query failed, using local: ${e.message}")
                localRepository.getAllInsurances()
            }
        }
    }

    override fun getActiveInsurances(): Flow<List<Insurance>> {
        return authRepository.currentUser.flatMapLatest { currentUser ->
            try {
                if (currentUser != null) {
                    println("SmartInsuranceRepository: User authenticated, getting active from cloud")
                    cloudRepository.getActiveInsurances()
                } else {
                    println("SmartInsuranceRepository: Guest mode, getting active from local")
                    localRepository.getActiveInsurances()
                }
            } catch (e: Exception) {
                println("SmartInsuranceRepository: Cloud query failed, using local: ${e.message}")
                localRepository.getActiveInsurances()
            }
        }
    }

    override fun getActiveInsurancesForUser(userId: String): Flow<List<Insurance>> {
        return authRepository.currentUser.flatMapLatest { currentUser ->
            try {
                if (currentUser != null) {
                    println("SmartInsuranceRepository: User authenticated ($userId), getting from cloud")
                    cloudRepository.getActiveInsurancesForUser(userId)
                } else {
                    println("SmartInsuranceRepository: Guest mode, getting from local")
                    localRepository.getActiveInsurancesForUser("guest")
                }
            } catch (e: Exception) {
                println("SmartInsuranceRepository: Cloud query failed for user $userId, using local: ${e.message}")
                localRepository.getActiveInsurancesForUser("guest")
            }
        }
    }
}