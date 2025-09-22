package com.insurancereminder.shared.repository

import com.insurancereminder.shared.model.Insurance
import kotlinx.coroutines.flow.Flow

/**
 * Interface for insurance repository
 * Can be implemented by both cloud and local repositories
 */
interface IInsuranceRepository {
    suspend fun addInsurance(insurance: Insurance): String
    suspend fun updateInsurance(insurance: Insurance)
    suspend fun deleteInsurance(insuranceId: String)
    suspend fun getInsurance(insuranceId: String): Insurance?
    fun getAllInsurances(): Flow<List<Insurance>>
    fun getActiveInsurances(): Flow<List<Insurance>>
    fun getActiveInsurancesForUser(userId: String): Flow<List<Insurance>>
}