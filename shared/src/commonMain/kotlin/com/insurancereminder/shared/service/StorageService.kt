package com.insurancereminder.shared.service

import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.StorageReference

class StorageService(private val storage: FirebaseStorage) {

    suspend fun uploadPolicyFile(
        userId: String,
        insuranceId: String,
        fileName: String,
        fileData: ByteArray
    ): Result<String> {
        return try {
            val sanitizedFileName = sanitizeFileName(fileName)
            val filePath = "policies/$userId/$insuranceId/$sanitizedFileName"

            // For now, return a placeholder URL until Firebase is properly configured
            val mockUrl = "mock://policies/$userId/$insuranceId/$sanitizedFileName"
            Result.success(mockUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePolicyFile(fileUrl: String): Result<Boolean> {
        return try {
            // For now, just return success until Firebase is properly configured
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sanitizeFileName(fileName: String): String {
        return fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
}