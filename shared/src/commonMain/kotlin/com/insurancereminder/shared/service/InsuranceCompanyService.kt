package com.insurancereminder.shared.service

import com.insurancereminder.shared.model.InsuranceCompany
import com.insurancereminder.shared.model.InsuranceType
import com.insurancereminder.shared.repository.InsuranceCompanyRepository
import com.insurancereminder.shared.data.LocalInsuranceCompanies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf

class InsuranceCompanyService(
    private val repository: InsuranceCompanyRepository
) {

    fun getAllCompanies(): Flow<List<InsuranceCompany>> {
        return repository.getAllCompanies()
            .map { companies ->
                companies.sortedBy { it.displayName }
            }
    }

    fun getCompaniesByCountry(country: String): Flow<List<InsuranceCompany>> {
        return repository.getCompaniesByCountry(country)
            .map { companies ->
                companies.sortedBy { it.displayName }
            }
    }

    fun getCompaniesByType(insuranceType: InsuranceType): Flow<List<InsuranceCompany>> {
        return repository.getCompaniesByType(insuranceType)
            .map { companies ->
                companies.sortedBy { it.displayName }
            }
    }

    fun getCompaniesByCountryAndType(country: String, insuranceType: InsuranceType): Flow<List<InsuranceCompany>> {
        // Use local data for immediate functionality across all platforms
        // When Firestore is set up, this can be switched to use repository.getCompaniesByCountry()
        return flowOf(LocalInsuranceCompanies.getCompaniesByCountryAndType(country, insuranceType))
    }

    suspend fun findCompanyByName(name: String): InsuranceCompany? {
        // This would be better with a search index in production
        return null // Simplified for now
    }

    companion object {
        fun getOtherCompany(): InsuranceCompany {
            return InsuranceCompany(
                id = "other",
                name = "other",
                displayName = "Other",
                logoUrl = "",
                country = "GLOBAL",
                supportedTypes = InsuranceType.values().toList(),
                websiteUrl = null,
                isActive = true
            )
        }
    }

}