package com.insurancereminder.shared.presentation

import com.insurancereminder.shared.model.Insurance
import com.insurancereminder.shared.util.daysUntilExpiry

data class InsuranceListState(
    val insurances: List<Insurance> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGuestMode: Boolean = true
) {
    val hasInsurances: Boolean get() = insurances.isNotEmpty()
    val canAddMoreInsurances: Boolean get() = !isGuestMode || insurances.size < 5

    fun getExpiringInsurances(daysThreshold: Int = 30): List<Insurance> {
        return insurances.filter { insurance ->
            val daysUntilExpiry = insurance.daysUntilExpiry
            daysUntilExpiry in 0..daysThreshold
        }
    }

    fun getExpiredInsurances(): List<Insurance> {
        return insurances.filter { insurance -> insurance.daysUntilExpiry < 0 }
    }
}