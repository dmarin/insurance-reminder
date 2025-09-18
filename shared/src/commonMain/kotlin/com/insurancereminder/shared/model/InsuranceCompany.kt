package com.insurancereminder.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class InsuranceCompany(
    val id: String = "",
    val name: String,
    val displayName: String,
    val logoUrl: String,
    val country: String,
    val supportedTypes: List<InsuranceType>,
    val websiteUrl: String? = null,
    val isActive: Boolean = true
)