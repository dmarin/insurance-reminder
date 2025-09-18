package com.insurancereminder.shared.di

import com.insurancereminder.shared.domain.usecase.AuthUseCases
import com.insurancereminder.shared.domain.usecase.InsuranceUseCases
import com.insurancereminder.shared.repository.AuthRepository
import com.insurancereminder.shared.repository.InsuranceRepository
import com.insurancereminder.shared.repository.InsuranceCompanyRepository
import com.insurancereminder.shared.repository.LocalInsuranceRepository
import com.insurancereminder.shared.repository.UnifiedInsuranceRepository
import com.insurancereminder.shared.service.ComparisonService
import com.insurancereminder.shared.service.InsuranceCompanyService
import com.insurancereminder.shared.service.StorageService
import com.insurancereminder.shared.database.InsuranceDatabase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage

/**
 * Shared dependency injection container
 * Provides all the shared business logic dependencies
 */
class SharedModule {

    // Repositories
    private val authRepository by lazy {
        AuthRepository(Firebase.auth, Firebase.firestore)
    }

    private val cloudInsuranceRepository by lazy {
        InsuranceRepository(Firebase.firestore)
    }

    // Note: For now using cloud repository until we set up local database properly
    // TODO: Implement proper UnifiedInsuranceRepository with local database
    private val insuranceRepository by lazy {
        cloudInsuranceRepository
    }

    private val insuranceCompanyRepository by lazy {
        InsuranceCompanyRepository(Firebase.firestore)
    }

    // Services
    private val storageService by lazy {
        StorageService(Firebase.storage)
    }

    private val comparisonService by lazy {
        ComparisonService()
    }

    private val insuranceCompanyService by lazy {
        InsuranceCompanyService(insuranceCompanyRepository)
    }


    // Use Cases
    val authUseCases by lazy {
        AuthUseCases(authRepository)
    }

    val insuranceUseCases by lazy {
        InsuranceUseCases(insuranceRepository, authRepository)
    }

    // Getters for services (if needed by platform-specific code)
    fun provideStorageService() = storageService
    fun provideComparisonService() = comparisonService
    fun provideInsuranceCompanyService() = insuranceCompanyService
    fun provideInsuranceRepository() = insuranceRepository
    fun provideAuthRepository() = authRepository
}