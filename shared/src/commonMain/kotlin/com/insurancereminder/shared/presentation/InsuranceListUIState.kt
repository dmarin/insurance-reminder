package com.insurancereminder.shared.presentation

import com.insurancereminder.shared.model.Insurance

data class InsuranceListUIState(
    val insurances: List<Insurance> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val groupedInsurances: Map<String, List<Insurance>> = emptyMap()
)

sealed class InsuranceListAction {
    object LoadInsurances : InsuranceListAction()
    data class DeleteInsurance(val insuranceId: String) : InsuranceListAction()
    data class RenewInsurance(val insurance: Insurance, val newExpiryDate: kotlinx.datetime.LocalDate) : InsuranceListAction()
    object ClearError : InsuranceListAction()
}

data class InsuranceGrouping(
    val category: String,
    val insurances: List<Insurance>
)

fun List<Insurance>.groupByCategory(): List<InsuranceGrouping> {
    return this.groupBy { it.type.category }
        .map { (category, insurances) ->
            InsuranceGrouping(
                category = category,
                insurances = insurances.sortedBy { it.expiryDate }
            )
        }
        .sortedBy { it.category }
}