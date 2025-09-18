package com.insurancereminder.shared.localization

import android.content.Context
import java.util.*

actual class StringProvider(private val context: Context) {
    actual fun getString(key: String): String {
        val resourceId = getStringResourceId(key)
        return if (resourceId != 0) {
            context.getString(resourceId)
        } else {
            key // Fallback to key if not found
        }
    }

    actual fun getString(key: String, vararg args: Any): String {
        val resourceId = getStringResourceId(key)
        return if (resourceId != 0) {
            context.getString(resourceId, *args)
        } else {
            key // Fallback to key if not found
        }
    }

    actual fun getCurrentLanguage(): String {
        return Locale.getDefault().language
    }

    private fun getStringResourceId(key: String): Int {
        return context.resources.getIdentifier(key, "string", context.packageName)
    }
}