package com.insurancereminder.shared.repository

import com.insurancereminder.shared.database.InsuranceDatabase
import com.insurancereminder.shared.model.Insurance
import com.insurancereminder.shared.model.InsuranceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlin.random.Random

class LocalInsuranceRepository(private val database: InsuranceDatabase) {

    fun getAllInsurances(): Flow<List<Insurance>> {
        return database.insuranceQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { insuranceList ->
                insuranceList.map { mapToInsurance(it) }
            }
    }

    fun getInsurancesForUser(userId: String): Flow<List<Insurance>> {
        return database.insuranceQueries.selectByUserId(userId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { insuranceList ->
                insuranceList.map { mapToInsurance(it) }
            }
    }

    suspend fun addInsurance(insurance: Insurance): String {
        val id = generateId()
        database.insuranceQueries.insertInsurance(
            id = id,
            type = insurance.type.name,
            name = insurance.name,
            expiryDate = insurance.expiryDate.toString(),
            reminderDaysBefore = insurance.reminderDaysBefore.toLong(),
            isActive = if (insurance.isActive) 1L else 0L,
            currentPrice = insurance.currentPrice,
            currency = insurance.currency,
            policyFileUrl = insurance.policyFileUrl,
            policyFileName = insurance.policyFileName,
            companyName = insurance.companyName,
            policyNumber = insurance.policyNumber,
            userId = insurance.userId,
            sharedWithUserId = insurance.sharedWithUserId,
            createdAt = insurance.createdAt?.toString(),
            updatedAt = insurance.updatedAt?.toString()
        )
        return id
    }

    suspend fun updateInsurance(insurance: Insurance) {
        database.insuranceQueries.updateInsurance(
            type = insurance.type.name,
            name = insurance.name,
            expiryDate = insurance.expiryDate.toString(),
            reminderDaysBefore = insurance.reminderDaysBefore.toLong(),
            isActive = if (insurance.isActive) 1L else 0L,
            currentPrice = insurance.currentPrice,
            currency = insurance.currency,
            policyFileUrl = insurance.policyFileUrl,
            policyFileName = insurance.policyFileName,
            companyName = insurance.companyName,
            policyNumber = insurance.policyNumber,
            sharedWithUserId = insurance.sharedWithUserId,
            updatedAt = insurance.updatedAt?.toString(),
            id = insurance.id
        )
    }

    suspend fun getInsurance(insuranceId: String): Insurance? {
        return try {
            val dbInsurance = database.insuranceQueries.selectById(insuranceId).executeAsOneOrNull()
            dbInsurance?.let { mapToInsurance(it) }
        } catch (e: Exception) {
            println("LocalInsuranceRepository: Failed to get insurance with ID $insuranceId: ${e.message}")
            null
        }
    }

    suspend fun deleteInsurance(insuranceId: String) {
        val currentTime = LocalDate.parse("2024-01-01") // You'll need proper date handling
        database.insuranceQueries.deleteInsurance(
            updatedAt = currentTime.toString(),
            id = insuranceId
        )
    }

    suspend fun exportData(userId: String): List<Insurance> {
        return database.insuranceQueries.selectByUserId(userId)
            .executeAsList()
            .map { mapToInsurance(it) }
    }

    suspend fun clearUserData(userId: String) {
        database.insuranceQueries.deleteAllForUser(userId)
    }

    private fun generateId(): String {
        return "local_${Clock.System.now().epochSeconds}_${Random.nextInt(0, 999)}"
    }

    private fun mapToInsurance(dbInsurance: com.insurancereminder.shared.database.Insurance): Insurance {
        println("LocalInsuranceRepository: Mapping insurance from DB - ID: '${dbInsurance.id}', name: '${dbInsurance.name}'")
        return Insurance(
            id = dbInsurance.id,
            type = InsuranceType.valueOf(dbInsurance.type),
            name = dbInsurance.name,
            expiryDate = LocalDate.parse(dbInsurance.expiryDate),
            reminderDaysBefore = dbInsurance.reminderDaysBefore.toInt(),
            isActive = dbInsurance.isActive == 1L,
            currentPrice = dbInsurance.currentPrice,
            currency = dbInsurance.currency,
            policyFileUrl = dbInsurance.policyFileUrl,
            policyFileName = dbInsurance.policyFileName,
            companyName = dbInsurance.companyName,
            policyNumber = dbInsurance.policyNumber,
            userId = dbInsurance.userId,
            sharedWithUserId = dbInsurance.sharedWithUserId,
            createdAt = dbInsurance.createdAt?.let { LocalDate.parse(it) },
            updatedAt = dbInsurance.updatedAt?.let { LocalDate.parse(it) }
        )
    }
}