package com.insurancereminder.shared.localization

actual class StringProvider {
    actual fun getString(key: String): String {
        return getLocalizedString(key)
    }

    actual fun getString(key: String, vararg args: Any): String {
        val format = getLocalizedString(key)
        return format // Simplified for JS
    }

    actual fun getCurrentLanguage(): String {
        return js("navigator.language || navigator.userLanguage || 'en'").toString().substring(0, 2)
    }

    private fun getLocalizedString(key: String): String {
        val lang = getCurrentLanguage()
        return when (lang) {
            "es" -> getSpanishString(key)
            else -> getEnglishString(key)
        }
    }

    private fun getSpanishString(key: String): String {
        return when (key) {
            "app_name" -> "Recordatorio de Seguros"
            "app_subtitle" -> "Gestiona tus pólizas de seguro con facilidad"
            "sign_in" -> "Iniciar Sesión"
            "sign_up" -> "Registrarse"
            "email" -> "Correo Electrónico"
            "password" -> "Contraseña"
            "confirm_password" -> "Confirmar Contraseña"
            "continue_as_guest" -> "Continuar como Invitado"
            "guest_mode_description" -> "Modo invitado: Almacena datos localmente (máximo 5 pólizas)"
            "insurance_reminders" -> "Recordatorios de Seguros"
            "add_insurance" -> "Añadir Seguro"
            "insurance_name" -> "Nombre del Seguro"
            "insurance_type" -> "Tipo de Seguro"
            "expiry_date" -> "Fecha de Expiración"
            "current_price" -> "Precio Actual (€)"
            "insurance_company" -> "Compañía de Seguros"
            "auto_insurance" -> "Seguro de Auto"
            "home_insurance" -> "Seguro de Hogar"
            "health_insurance" -> "Seguro de Salud"
            "life_insurance" -> "Seguro de Vida"
            "travel_insurance" -> "Seguro de Viaje"
            "business_insurance" -> "Seguro de Empresa"
            "pet_insurance" -> "Seguro de Mascotas"
            "dental_insurance" -> "Seguro Dental"
            "vision_insurance" -> "Seguro de Vista"
            "other_insurance" -> "Otro"
            "profile" -> "Perfil"
            "free_tier" -> "Nivel Gratuito"
            "premium_tier" -> "Nivel Premium"
            "upgrade_to_premium" -> "Actualizar a Premium"
            "back" -> "Atrás"
            else -> key
        }
    }

    private fun getEnglishString(key: String): String {
        return when (key) {
            "app_name" -> "Insurance Reminder"
            "app_subtitle" -> "Manage your insurance policies with ease"
            "sign_in" -> "Sign In"
            "sign_up" -> "Sign Up"
            "email" -> "Email"
            "password" -> "Password"
            "confirm_password" -> "Confirm Password"
            "continue_as_guest" -> "Continue as Guest"
            "guest_mode_description" -> "Guest mode: Store data locally (max 5 policies)"
            "insurance_reminders" -> "Insurance Reminders"
            "add_insurance" -> "Add Insurance"
            "insurance_name" -> "Insurance Name"
            "insurance_type" -> "Insurance Type"
            "expiry_date" -> "Expiry Date"
            "current_price" -> "Current Price (€)"
            "insurance_company" -> "Insurance Company"
            "auto_insurance" -> "Auto Insurance"
            "home_insurance" -> "Home Insurance"
            "health_insurance" -> "Health Insurance"
            "life_insurance" -> "Life Insurance"
            "travel_insurance" -> "Travel Insurance"
            "business_insurance" -> "Business Insurance"
            "pet_insurance" -> "Pet Insurance"
            "dental_insurance" -> "Dental Insurance"
            "vision_insurance" -> "Vision Insurance"
            "other_insurance" -> "Other"
            "profile" -> "Profile"
            "free_tier" -> "Free Tier"
            "premium_tier" -> "Premium Tier"
            "upgrade_to_premium" -> "Upgrade to Premium"
            "back" -> "Back"
            else -> key
        }
    }
}