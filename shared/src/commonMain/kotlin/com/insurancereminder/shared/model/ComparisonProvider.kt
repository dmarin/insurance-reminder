package com.insurancereminder.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class ComparisonProvider(
    val displayName: String,
    val baseUrl: String,
    val supportedTypes: List<InsuranceType>
) {
    COMPARATOR(
        "Comparator.es",
        "https://www.comparator.es",
        listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH)
    ),
    RASTREATOR(
        "Rastreator.com",
        "https://www.rastreator.com",
        listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH, InsuranceType.LIFE)
    ),
    ACIERTO(
        "Acierto.com",
        "https://www.acierto.com",
        listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH, InsuranceType.LIFE)
    ),
    KELISTO(
        "Kelisto.es",
        "https://www.kelisto.es",
        listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH)
    )
}

@Serializable
data class ComparisonQuote(
    val providerId: String,
    val companyName: String,
    val price: Double,
    val currency: String,
    val features: List<String>,
    val url: String
)