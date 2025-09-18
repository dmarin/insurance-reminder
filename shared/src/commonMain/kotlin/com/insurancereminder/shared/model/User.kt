package com.insurancereminder.shared.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String,
    val displayName: String? = null,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val partnerId: String? = null,
    val createdAt: LocalDateTime? = null,
    val subscriptionExpiresAt: LocalDateTime? = null
) {
    val isPremium: Boolean get() = subscriptionTier == SubscriptionTier.PREMIUM
}

@Serializable
enum class SubscriptionTier(val displayName: String, val maxInsurances: Int) {
    FREE("Free", 5),
    PREMIUM("Premium", Int.MAX_VALUE)
}

@Serializable
data class PolicyFile(
    val id: String = "",
    val insuranceId: String,
    val fileName: String,
    val fileUrl: String,
    val fileSize: Long,
    val mimeType: String,
    val uploadedAt: LocalDateTime? = null
)