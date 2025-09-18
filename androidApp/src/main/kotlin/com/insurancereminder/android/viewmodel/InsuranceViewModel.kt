package com.insurancereminder.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insurancereminder.shared.di.SharedModule
import com.insurancereminder.shared.model.InsuranceType
import com.insurancereminder.shared.presentation.InsuranceListState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class InsuranceViewModel(
    private val sharedModule: SharedModule = SharedModule()
) : ViewModel() {

    private val insuranceUseCases = sharedModule.insuranceUseCases

    private val _state = MutableStateFlow(InsuranceListState())
    val state: StateFlow<InsuranceListState> = _state.asStateFlow()

    init {
        loadInsurances()
    }

    private fun loadInsurances() {
        viewModelScope.launch {
            try {
                android.util.Log.d("InsuranceViewModel", "Starting to load insurances...")
                _state.value = _state.value.copy(isLoading = true, error = null)

                insuranceUseCases.getActiveInsurancesForCurrentUser().collect { insuranceList ->
                    android.util.Log.d("InsuranceViewModel", "Received Flow emission with ${insuranceList.size} insurances")
                    insuranceList.forEach { insurance ->
                        android.util.Log.d("InsuranceViewModel", "Insurance: ${insurance.name}, ID: ${insurance.id}, UserID: ${insurance.userId}")
                    }
                    _state.value = _state.value.copy(
                        insurances = insuranceList,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("InsuranceViewModel", "Error loading insurances", e)
                _state.value = _state.value.copy(
                    error = "Failed to load insurances: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun addInsurance(
        name: String,
        type: InsuranceType,
        expiryDate: LocalDate,
        reminderDaysBefore: Int = 30,
        currentPrice: Double? = null,
        companyName: String? = null,
        companyId: String? = null,
        companyLogoUrl: String? = null,
        policyNumber: String? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = insuranceUseCases.addInsurance(
                name = name,
                type = type,
                expiryDate = expiryDate,
                reminderDaysBefore = reminderDaysBefore,
                currentPrice = currentPrice,
                companyName = companyName,
                companyId = companyId,
                companyLogoUrl = companyLogoUrl,
                policyNumber = policyNumber
            )

            _state.value = _state.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )

            if (result.isSuccess) {
                android.util.Log.d("InsuranceViewModel", "Insurance added successfully: ${result.getOrNull()}")
                onSuccess?.invoke()
            } else {
                android.util.Log.e("InsuranceViewModel", "Failed to add insurance: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun deleteInsurance(insuranceId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = insuranceUseCases.deleteInsurance(insuranceId)

            _state.value = _state.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun renewInsurance(insuranceId: String, newExpiryDate: LocalDate) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = insuranceUseCases.renewInsurance(insuranceId, newExpiryDate)

            _state.value = _state.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )

            if (result.isSuccess) {
                android.util.Log.d("InsuranceViewModel", "Insurance renewed successfully")
            } else {
                android.util.Log.e("InsuranceViewModel", "Failed to renew insurance: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    // Convenience accessors for the UI
    val insurances: StateFlow<List<com.insurancereminder.shared.model.Insurance>> =
        _state.map { it.insurances }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val isLoading: StateFlow<Boolean> =
        _state.map { it.isLoading }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val error: StateFlow<String?> =
        _state.map { it.error }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
}