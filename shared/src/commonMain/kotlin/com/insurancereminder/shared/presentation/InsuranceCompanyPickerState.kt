package com.insurancereminder.shared.presentation

import com.insurancereminder.shared.model.InsuranceCompany
import com.insurancereminder.shared.model.InsuranceType

data class InsuranceCompanyPickerState(
    val availableCompanies: List<InsuranceCompany> = emptyList(),
    val selectedCompany: InsuranceCompany? = null,
    val isOtherSelected: Boolean = false,
    val customCompanyName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class InsuranceCompanyPickerAction {
    data class SelectCompany(val company: InsuranceCompany) : InsuranceCompanyPickerAction()
    object SelectOther : InsuranceCompanyPickerAction()
    data class SetCustomCompanyName(val name: String) : InsuranceCompanyPickerAction()
    data class LoadCompanies(val country: String, val insuranceType: InsuranceType) : InsuranceCompanyPickerAction()
}