package com.insurancereminder.shared.data

import com.insurancereminder.shared.model.InsuranceCompany
import com.insurancereminder.shared.model.InsuranceType

/**
 * Local fallback data for insurance companies while Firestore is being set up.
 * This provides immediate functionality across all platforms.
 */
object LocalInsuranceCompanies {

    fun getSpanishCompanies(): List<InsuranceCompany> {
        return listOf(
            // Major Spanish Insurance Companies
            InsuranceCompany(
                id = "mapfre",
                name = "mapfre",
                displayName = "MAPFRE",
                logoUrl = "https://www.mapfre.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH, InsuranceType.LIFE, InsuranceType.MOTORCYCLE),
                websiteUrl = "https://www.mapfre.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "axa",
                name = "axa",
                displayName = "AXA España",
                logoUrl = "https://www.axa.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH, InsuranceType.LIFE),
                websiteUrl = "https://www.axa.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "allianz",
                name = "allianz",
                displayName = "Allianz Seguros",
                logoUrl = "https://www.allianz.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.LIFE, InsuranceType.MOTORCYCLE),
                websiteUrl = "https://www.allianz.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "mutua_madrilena",
                name = "mutua_madrilena",
                displayName = "Mutua Madrileña",
                logoUrl = "https://www.mutua.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH),
                websiteUrl = "https://www.mutua.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "reale_seguros",
                name = "reale_seguros",
                displayName = "Reale Seguros",
                logoUrl = "https://www.reale.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.MOTORCYCLE),
                websiteUrl = "https://www.reale.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "generali",
                name = "generali",
                displayName = "Generali España",
                logoUrl = "https://www.generali.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.LIFE, InsuranceType.HEALTH),
                websiteUrl = "https://www.generali.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "zurich",
                name = "zurich",
                displayName = "Zurich Seguros",
                logoUrl = "https://www.zurich.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.LIFE),
                websiteUrl = "https://www.zurich.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "liberty_seguros",
                name = "liberty_seguros",
                displayName = "Liberty Seguros",
                logoUrl = "https://www.libertyseguros.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.MOTORCYCLE),
                websiteUrl = "https://www.libertyseguros.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "santalucia",
                name = "santalucia",
                displayName = "Santa Lucía Seguros",
                logoUrl = "https://www.santalucia.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.LIFE, InsuranceType.HEALTH),
                websiteUrl = "https://www.santalucia.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "catalana_occidente",
                name = "catalana_occidente",
                displayName = "Catalana Occidente",
                logoUrl = "https://www.catalanaoccidente.com/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.MOTORCYCLE),
                websiteUrl = "https://www.catalanaoccidente.com",
                isActive = true
            ),
            InsuranceCompany(
                id = "pelayo",
                name = "pelayo",
                displayName = "Pelayo Seguros",
                logoUrl = "https://www.pelayo.com/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.MOTORCYCLE),
                websiteUrl = "https://www.pelayo.com",
                isActive = true
            ),
            InsuranceCompany(
                id = "caser",
                name = "caser",
                displayName = "Caser Seguros",
                logoUrl = "https://www.caser.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH, InsuranceType.LIFE),
                websiteUrl = "https://www.caser.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "linea_directa",
                name = "linea_directa",
                displayName = "Línea Directa",
                logoUrl = "https://www.lineadirecta.com/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.MOTORCYCLE),
                websiteUrl = "https://www.lineadirecta.com",
                isActive = true
            ),
            InsuranceCompany(
                id = "race",
                name = "race",
                displayName = "RACE Seguros",
                logoUrl = "https://www.race.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.MOTORCYCLE),
                websiteUrl = "https://www.race.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "dkv",
                name = "dkv",
                displayName = "DKV Seguros",
                logoUrl = "https://www.dkvseguros.com/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.HEALTH, InsuranceType.DENTAL),
                websiteUrl = "https://www.dkvseguros.com",
                isActive = true
            ),
            InsuranceCompany(
                id = "asisa",
                name = "asisa",
                displayName = "ASISA",
                logoUrl = "https://www.asisa.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.HEALTH, InsuranceType.DENTAL),
                websiteUrl = "https://www.asisa.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "adeslas",
                name = "adeslas",
                displayName = "Adeslas",
                logoUrl = "https://www.adeslas.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.HEALTH, InsuranceType.DENTAL),
                websiteUrl = "https://www.adeslas.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "sanitas",
                name = "sanitas",
                displayName = "Sanitas",
                logoUrl = "https://www.sanitas.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.HEALTH, InsuranceType.DENTAL),
                websiteUrl = "https://www.sanitas.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "fiatc",
                name = "fiatc",
                displayName = "FIATC Seguros",
                logoUrl = "https://www.fiatc.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH, InsuranceType.MOTORCYCLE),
                websiteUrl = "https://www.fiatc.es",
                isActive = true
            ),
            InsuranceCompany(
                id = "segurcaixa_adeslas",
                name = "segurcaixa_adeslas",
                displayName = "SegurCaixa Adeslas",
                logoUrl = "https://www.segurcaixaadeslas.es/favicon.ico",
                country = "ES",
                supportedTypes = listOf(InsuranceType.AUTO, InsuranceType.HOME, InsuranceType.HEALTH, InsuranceType.LIFE),
                websiteUrl = "https://www.segurcaixaadeslas.es",
                isActive = true
            )
        )
    }

    fun getCompaniesByCountry(country: String): List<InsuranceCompany> {
        return when (country.uppercase()) {
            "ES" -> getSpanishCompanies()
            else -> emptyList()
        }
    }

    fun getCompaniesByCountryAndType(country: String, insuranceType: InsuranceType): List<InsuranceCompany> {
        return getCompaniesByCountry(country).filter { company ->
            company.supportedTypes.contains(insuranceType)
        }.sortedBy { it.displayName }
    }
}