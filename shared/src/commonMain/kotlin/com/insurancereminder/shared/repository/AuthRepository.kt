package com.insurancereminder.shared.repository

import com.insurancereminder.shared.model.User
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")

    val currentUser: Flow<FirebaseUser?> = auth.authStateChanged

    suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password)
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    displayName = firebaseUser.displayName
                )

                usersCollection.document(firebaseUser.uid).set(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password)
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val userDoc = usersCollection.document(firebaseUser.uid).get()
                val user = userDoc.data<User>()
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to sign in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return try {
            val userDoc = usersCollection.document(firebaseUser.uid).get()
            userDoc.data<User>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserSubscription(userId: String, user: User) {
        usersCollection.document(userId).set(user)
    }

    suspend fun shareWithPartner(userId: String, partnerEmail: String): Result<Boolean> {
        return try {
            val partnerQuery = usersCollection.where("email", equalTo = partnerEmail).get()
            val partnerDoc = partnerQuery.documents.firstOrNull()

            if (partnerDoc != null) {
                val currentUser = getCurrentUser()
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(partnerId = partnerDoc.id)
                    updateUserSubscription(userId, updatedUser)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Current user not found"))
                }
            } else {
                Result.failure(Exception("Partner not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}