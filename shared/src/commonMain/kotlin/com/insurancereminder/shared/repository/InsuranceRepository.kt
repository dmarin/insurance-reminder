package com.insurancereminder.shared.repository

import com.insurancereminder.shared.model.Insurance
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.orderBy
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InsuranceRepository(private val firestore: FirebaseFirestore) {
    private val collection = firestore.collection("insurances")

    suspend fun addInsurance(insurance: Insurance): String {
        val docRef = collection.add(insurance)
        return docRef.id
    }

    suspend fun updateInsurance(insurance: Insurance) {
        collection.document(insurance.id).set(insurance)
    }

    suspend fun deleteInsurance(insuranceId: String) {
        collection.document(insuranceId).delete()
    }

    suspend fun getInsurance(insuranceId: String): Insurance? {
        return try {
            collection.document(insuranceId).get().data()
        } catch (e: Exception) {
            null
        }
    }

    fun getAllInsurances(): Flow<List<Insurance>> {
        return collection
            .orderBy("expiryDate")
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    try {
                        val insurance = document.data<Insurance>()
                        insurance.copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    fun getActiveInsurances(): Flow<List<Insurance>> {
        return collection
            .where("isActive", equalTo = true)
            .orderBy("expiryDate")
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    try {
                        val insurance = document.data<Insurance>()
                        insurance.copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    fun getActiveInsurancesForUser(userId: String): Flow<List<Insurance>> {
        println("InsuranceRepository: Querying for userId: $userId")
        return collection
            .where("userId", equalTo = userId)
            .snapshots
            .map { snapshot ->
                println("InsuranceRepository: Got ${snapshot.documents.size} documents")
                snapshot.documents.mapNotNull { document ->
                    try {
                        val insurance = document.data<Insurance>()
                        println("InsuranceRepository: Parsed insurance: ${insurance.name} (active: ${insurance.isActive})")
                        insurance.copy(id = document.id)
                    } catch (e: Exception) {
                        println("InsuranceRepository: Error parsing document: ${e.message}")
                        null
                    }
                }.filter { it.isActive } // Filter in code instead of query to avoid index issues
                .sortedBy { it.expiryDate }
            }
    }
}