package com.insurancereminder.shared.localization

expect class StringProvider {
    fun getString(key: String): String
    fun getString(key: String, vararg args: Any): String
    fun getCurrentLanguage(): String
}

object LocalizedStrings {
    lateinit var provider: StringProvider

    fun get(key: String): String = provider.getString(key)
    fun get(key: String, vararg args: Any): String = provider.getString(key, *args)
    fun getCurrentLanguage(): String = provider.getCurrentLanguage()
}