package com.insurancereminder.shared.service

import com.insurancereminder.shared.model.Insurance
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExportService {

    fun exportToJson(insurances: List<Insurance>): String {
        return Json.encodeToString(insurances)
    }

    fun importFromJson(jsonString: String): List<Insurance> {
        return try {
            Json.decodeFromString<List<Insurance>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun exportToCsv(insurances: List<Insurance>): String {
        val header = "Name,Type,Company,Policy Number,Expiry Date,Price,Currency,Has File\n"
        val rows = insurances.joinToString("\n") { insurance ->
            listOf(
                insurance.name,
                insurance.type.displayName,
                insurance.companyName ?: "",
                insurance.policyNumber ?: "",
                insurance.expiryDate.toString(),
                insurance.currentPrice?.toString() ?: "",
                insurance.currency,
                if (insurance.policyFileUrl != null) "Yes" else "No"
            ).joinToString(",") { field ->
                "\"${field.replace("\"", "\"\"")}\""
            }
        }
        return header + rows
    }
}