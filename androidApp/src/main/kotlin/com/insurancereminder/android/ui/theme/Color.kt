package com.insurancereminder.android.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.insurancereminder.shared.model.InsuranceType
import com.insurancereminder.shared.theme.InsuranceThemeMapping
import com.insurancereminder.shared.theme.InsuranceThemeRole

// Material 3 Blue Theme Colors (Light)
val Blue40 = Color(0xFF1976D2)
val BlueGrey40 = Color(0xFF455A64)
val LightBlue40 = Color(0xFF0288D1)

// Material 3 Blue Theme Colors (Dark)
val Blue80 = Color(0xFF90CAF9)
val BlueGrey80 = Color(0xFFB0BEC5)
val LightBlue80 = Color(0xFF81D4FA)

// Legacy colors for backward compatibility
val Purple80 = Blue80
val PurpleGrey80 = BlueGrey80
val Pink80 = LightBlue80

val Purple40 = Blue40
val PurpleGrey40 = BlueGrey40
val Pink40 = LightBlue40

@Composable
fun getInsuranceTypeColor(type: InsuranceType): Color {
    val colorScheme = androidx.compose.material3.MaterialTheme.colorScheme
    val themeRole = InsuranceThemeMapping.getThemeRole(type)

    return when (themeRole) {
        InsuranceThemeRole.PRIMARY -> colorScheme.primary
        InsuranceThemeRole.SECONDARY -> colorScheme.secondary
        InsuranceThemeRole.SUCCESS -> colorScheme.primary
        InsuranceThemeRole.ERROR -> colorScheme.error
        InsuranceThemeRole.WARNING -> colorScheme.tertiary
        InsuranceThemeRole.INFO -> colorScheme.secondary
        InsuranceThemeRole.SURFACE -> colorScheme.surface
        InsuranceThemeRole.OUTLINE -> colorScheme.outline
    }
}