package com.insurancereminder.shared.repository

import com.insurancereminder.shared.model.Insurance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.random.Random

/**
 * Mock local repository that stores insurances in memory
 * This is a temporary solution until SQLDelight database is properly set up
 */
class MockLocalInsuranceRepository : IInsuranceRepository {
    private val insurances = MutableStateFlow<List<Insurance>>(emptyList())

    override suspend fun addInsurance(insurance: Insurance): String {
        val id = generateId()
        val insuranceWithId = insurance.copy(
            id = id,
            createdAt = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            updatedAt = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            isActive = true
        )

        val currentList = insurances.value
        insurances.value = currentList + insuranceWithId
        println("MockLocalInsuranceRepository: Added insurance ${insuranceWithId.name} with ID $id for user ${insuranceWithId.userId}")
        println("MockLocalInsuranceRepository: Total insurances: ${insurances.value.size}")

        return id
    }

    override suspend fun updateInsurance(insurance: Insurance) {
        val updatedInsurance = insurance.copy(
            updatedAt = Clock.System.todayIn(TimeZone.currentSystemDefault())
        )

        val currentList = insurances.value
        val updatedList = currentList.map {
            if (it.id == insurance.id) updatedInsurance else it
        }
        insurances.value = updatedList

        println("MockLocalInsuranceRepository: Updated insurance ${insurance.name}")
    }

    override suspend fun deleteInsurance(insuranceId: String) {
        val currentList = insurances.value
        val updatedList = currentList.map {
            if (it.id == insuranceId) it.copy(isActive = false) else it
        }
        insurances.value = updatedList

        println("MockLocalInsuranceRepository: Deleted insurance $insuranceId")
    }

    override suspend fun getInsurance(insuranceId: String): Insurance? {
        return insurances.value.find { it.id == insuranceId && it.isActive }
    }

    override fun getAllInsurances(): Flow<List<Insurance>> {
        return insurances.map { list ->
            list.filter { it.isActive }
                .sortedBy { it.expiryDate }
        }
    }

    override fun getActiveInsurances(): Flow<List<Insurance>> {
        return getAllInsurances()
    }

    override fun getActiveInsurancesForUser(userId: String): Flow<List<Insurance>> {
        println("MockLocalInsuranceRepository: Querying for userId: $userId")
        return insurances.map { list ->
            val filtered = list.filter { it.isActive && it.userId == userId }
                .sortedBy { it.expiryDate }
            println("MockLocalInsuranceRepository: Found ${filtered.size} insurances for user $userId")
            filtered.forEach { insurance ->
                println("  - ${insurance.name} (ID: ${insurance.id})")
            }
            filtered
        }
    }

    private fun generateId(): String {
        return "mock_${Clock.System.now().epochSeconds}_${Random.nextInt(0, 9999)}"
    }
}