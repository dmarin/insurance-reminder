package com.insurancereminder.shared.repository

import com.insurancereminder.shared.model.InsuranceCompany
import com.insurancereminder.shared.model.InsuranceType
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InsuranceCompanyRepository(private val firestore: FirebaseFirestore) {
    private val collection = firestore.collection("insurance_companies")

    fun getAllCompanies(): Flow<List<InsuranceCompany>> {
        return collection
            .where("isActive", equalTo = true)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    try {
                        val company = document.data<InsuranceCompany>()
                        company.copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    fun getCompaniesByCountry(country: String): Flow<List<InsuranceCompany>> {
        return collection
            .where("country", equalTo = country)
            .where("isActive", equalTo = true)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    try {
                        val company = document.data<InsuranceCompany>()
                        company.copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    fun getCompaniesByType(insuranceType: InsuranceType): Flow<List<InsuranceCompany>> {
        return getAllCompanies().map { companies ->
            companies.filter { company ->
                company.supportedTypes.contains(insuranceType)
            }
        }
    }

    suspend fun addCompany(company: InsuranceCompany): String {
        val docRef = collection.add(company)
        return docRef.id
    }

    suspend fun getCompany(companyId: String): InsuranceCompany? {
        return try {
            collection.document(companyId).get().data()
        } catch (e: Exception) {
            null
        }
    }
}