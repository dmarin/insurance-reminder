package com.insurancereminder.shared.service

import com.insurancereminder.shared.model.ComparisonProvider
import com.insurancereminder.shared.model.Insurance
import com.insurancereminder.shared.model.InsuranceType

class ComparisonService {

    fun getAvailableProviders(insuranceType: InsuranceType): List<ComparisonProvider> {
        return ComparisonProvider.values().filter { provider ->
            provider.supportedTypes.contains(insuranceType)
        }
    }

    fun generateComparisonUrl(
        provider: ComparisonProvider,
        insurance: Insurance
    ): String {
        return when (provider) {
            ComparisonProvider.COMPARATOR -> buildComparatorUrl(insurance)
            ComparisonProvider.RASTREATOR -> buildRastreatorUrl(insurance)
            ComparisonProvider.ACIERTO -> buildAciertoUrl(insurance)
            ComparisonProvider.KELISTO -> buildKelistoUrl(insurance)
        }
    }

    private fun buildComparatorUrl(insurance: Insurance): String {
        val baseUrl = "https://www.comparator.es"
        return when (insurance.type) {
            InsuranceType.AUTO -> "$baseUrl/seguros-de-coche"
            InsuranceType.HOME -> "$baseUrl/seguros-de-hogar"
            InsuranceType.HEALTH -> "$baseUrl/seguros-de-salud"
            else -> baseUrl
        }
    }

    private fun buildRastreatorUrl(insurance: Insurance): String {
        val baseUrl = "https://www.rastreator.com"
        return when (insurance.type) {
            InsuranceType.AUTO -> "$baseUrl/seguros-coche"
            InsuranceType.HOME -> "$baseUrl/seguros-hogar"
            InsuranceType.HEALTH -> "$baseUrl/seguros-salud"
            InsuranceType.LIFE -> "$baseUrl/seguros-vida"
            else -> baseUrl
        }
    }

    private fun buildAciertoUrl(insurance: Insurance): String {
        val baseUrl = "https://www.acierto.com"
        return when (insurance.type) {
            InsuranceType.AUTO -> "$baseUrl/seguros/coche"
            InsuranceType.HOME -> "$baseUrl/seguros/hogar"
            InsuranceType.HEALTH -> "$baseUrl/seguros/salud"
            InsuranceType.LIFE -> "$baseUrl/seguros/vida"
            else -> baseUrl
        }
    }

    private fun buildKelistoUrl(insurance: Insurance): String {
        val baseUrl = "https://www.kelisto.es"
        return when (insurance.type) {
            InsuranceType.AUTO -> "$baseUrl/seguros-coche"
            InsuranceType.HOME -> "$baseUrl/seguros-hogar"
            InsuranceType.HEALTH -> "$baseUrl/seguros-salud"
            else -> baseUrl
        }
    }
}