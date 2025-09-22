import androidx.compose.runtime.*
import com.insurancereminder.shared.localization.LocalizedStrings
import com.insurancereminder.shared.localization.StringProvider
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

fun main() {
    // Initialize localization
    LocalizedStrings.provider = WebStringProvider()

    renderComposable(rootElementId = "root") {
        InsuranceReminderWebApp()
    }
}

@Composable
fun InsuranceReminderWebApp() {
    var currentView by remember { mutableStateOf("login") }

    Style(AppStyles)

    Div(attrs = { classes(AppStyles.container) }) {
        Header()

        Div(attrs = {
            style {
                padding(20.px)
            }
        }) {
            when (currentView) {
                "login" -> AuthView(
                    onLoginSuccess = { currentView = "insurances" },
                    onGuestMode = { currentView = "insurances" }
                )
                "insurances" -> InsuranceListView(
                    onAddInsurance = { currentView = "add" },
                    onProfile = { currentView = "profile" }
                )
                "add" -> AddInsuranceView(
                    onBack = { currentView = "insurances" }
                )
                "profile" -> ProfileView(
                    onBack = { currentView = "insurances" }
                )
            }
        }
    }
}

@Composable
fun Header() {
    Header(attrs = {
        classes(AppStyles.header)
        style {
            backgroundColor(Color("var(--color-primary)"))
            color(Color("var(--color-on-primary)"))
            padding(20.px)
            marginBottom(0.px)
            borderBottom(0.px)
        }
    }) {
        H1(attrs = {
            style {
                color(Color("var(--color-on-primary)"))
                margin(0.px)
                fontSize(28.px)
                fontWeight("600")
            }
        }) {
            Text("🛡️ " + LocalizedStrings.get("app_name"))
        }
        P(attrs = {
            classes(AppStyles.subtitle)
            style {
                color(Color("var(--color-on-primary)"))
                opacity(0.9)
                margin(8.px, 0.px, 0.px, 0.px)
            }
        }) {
            Text(LocalizedStrings.get("app_subtitle"))
        }
    }
}

@Composable
fun AuthView(
    onLoginSuccess: () -> Unit,
    onGuestMode: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Div(attrs = { classes(AppStyles.authCard) }) {
        H2 {
            Text(if (isLoginMode) LocalizedStrings.get("sign_in") else LocalizedStrings.get("sign_up"))
        }

        Form {
            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("email")) }
                Input(type = InputType.Email) {
                    value(email)
                    onInput { email = it.value }
                    classes(AppStyles.formInput)
                }
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("password")) }
                Input(type = InputType.Password) {
                    value(password)
                    onInput { password = it.value }
                    classes(AppStyles.formInput)
                }
            }

            if (!isLoginMode) {
                Div(attrs = { classes(AppStyles.formGroup) }) {
                    Label { Text(LocalizedStrings.get("confirm_password")) }
                    Input(type = InputType.Password) {
                        value(confirmPassword)
                        onInput { confirmPassword = it.value }
                        classes(AppStyles.formInput)
                    }
                }
            }

            Button(
                attrs = {
                    classes(AppStyles.primaryButton)
                    onClick { onLoginSuccess() }
                }
            ) {
                Text(if (isLoginMode) LocalizedStrings.get("sign_in") else LocalizedStrings.get("sign_up"))
            }

            Button(
                attrs = {
                    classes(AppStyles.textButton)
                    onClick { isLoginMode = !isLoginMode }
                }
            ) {
                Text(
                    if (isLoginMode) "¿No tienes una cuenta? Registrarse"
                    else "¿Ya tienes una cuenta? Iniciar Sesión"
                )
            }

            Hr()

            Button(
                attrs = {
                    classes(AppStyles.secondaryButton)
                    onClick { onGuestMode() }
                }
            ) {
                Text(LocalizedStrings.get("continue_as_guest"))
            }

            P(attrs = { classes(AppStyles.description) }) {
                Text(LocalizedStrings.get("guest_mode_description"))
            }
        }
    }
}

@Composable
fun InsuranceListView(
    onAddInsurance: () -> Unit,
    onProfile: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var insuranceToDelete by remember { mutableStateOf<SampleInsurance?>(null) }

    // Sample insurance data for demonstration
    var sampleInsurances by remember { mutableStateOf(
        listOf(
            SampleInsurance(
                id = "1",
                name = "Honda Civic 2019",
                type = "AUTO",
                typeName = "Seguro de Auto",
                expiryDate = "2024-03-15",
                daysUntilExpiry = 45,
                price = 850.0,
                companyName = "Mapfre",
                status = "ACTIVE"
            ),
            SampleInsurance(
                id = "2",
                name = "Piso Madrid Centro",
                type = "HOME",
                typeName = "Seguro de Hogar",
                expiryDate = "2024-02-28",
                daysUntilExpiry = 28,
                price = 320.0,
                companyName = "Allianz",
                status = "EXPIRING_SOON"
            ),
            SampleInsurance(
                id = "3",
                name = "Seguro Salud Familiar",
                type = "HEALTH",
                typeName = "Seguro de Salud",
                expiryDate = "2024-06-10",
                daysUntilExpiry = 120,
                price = 1200.0,
                companyName = "Sanitas",
                status = "ACTIVE"
            )
        )
    )}

    val groupedInsurances = remember(sampleInsurances) {
        sampleInsurances.groupBy { insurance ->
            when (insurance.type) {
                "AUTO", "MOTORCYCLE" -> "Vehicle"
                "HOME" -> "Property"
                "HEALTH", "DENTAL" -> "Health"
                "LIFE", "PET" -> "Life & Family"
                "TRAVEL" -> "Travel"
                else -> "Other"
            }
        }
    }

    Div(attrs = { classes(AppStyles.toolbar) }) {
        H2 { Text(LocalizedStrings.get("insurance_reminders")) }
        Div(attrs = { classes(AppStyles.toolbarActions) }) {
            Button(
                attrs = {
                    classes(AppStyles.iconButton)
                    onClick { onProfile() }
                }
            ) {
                Text("👤")
            }
            Button(
                attrs = {
                    classes(AppStyles.primaryButton)
                    onClick { onAddInsurance() }
                }
            ) {
                Text("+ " + LocalizedStrings.get("add_insurance"))
            }
        }
    }

    if (sampleInsurances.isEmpty()) {
        Div(attrs = { classes(AppStyles.emptyState) }) {
            Div(attrs = { classes(AppStyles.emptyIcon) }) {
                Text("🛡️")
            }
            H3 { Text("No hay seguros añadidos") }
            P { Text("Haz clic en '+ Añadir Seguro' para comenzar") }
        }
    } else {
        groupedInsurances.forEach { (category, insurances) ->
            Div(attrs = { classes(AppStyles.categorySection) }) {
                Div(attrs = { classes(AppStyles.categoryHeader) }) {
                    H3 { Text(category) }
                }

                Div(attrs = { classes(AppStyles.insuranceGrid) }) {
                    insurances.forEach { insurance ->
                        InsuranceCard(
                            insurance = insurance,
                            onEdit = { /* Edit action */ },
                            onRenew = { /* Renew action */ },
                            onDelete = {
                                insuranceToDelete = insurance
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && insuranceToDelete != null) {
        Div(attrs = {
            style {
                position(Position.Fixed)
                top(0.px)
                left(0.px)
                width(100.percent)
                height(100.percent)
                backgroundColor(Color("rgba(0, 0, 0, 0.5)"))
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                zIndex(1000)
            }
        }) {
            Div(attrs = {
                style {
                    backgroundColor(Color("var(--color-surface)"))
                    padding(24.px)
                    borderRadius(12.px)
                    maxWidth(400.px)
                    margin(20.px)
                    boxShadow("0 4px 16px rgba(0, 0, 0, 0.2)")
                }
            }) {
                H3 { Text("Delete Insurance") }
                P { Text("Are you sure you want to delete ${insuranceToDelete?.name}? This action cannot be undone.") }
                Div(attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        gap(12.px)
                        marginTop(20.px)
                        justifyContent(JustifyContent.FlexEnd)
                    }
                }) {
                    Button(attrs = {
                        classes(AppStyles.secondaryButton)
                        onClick {
                            showDeleteDialog = false
                            insuranceToDelete = null
                        }
                    }) { Text("Cancel") }
                    Button(attrs = {
                        style {
                            backgroundColor(Color("var(--color-error)"))
                            color(Color("white"))
                            border(0.px)
                            padding(8.px, 16.px)
                            borderRadius(4.px)
                            cursor("pointer")
                        }
                        onClick {
                            insuranceToDelete?.let { insurance ->
                                sampleInsurances = sampleInsurances.filter { it.id != insurance.id }
                            }
                            showDeleteDialog = false
                            insuranceToDelete = null
                        }
                    }) { Text("Delete") }
                }
            }
        }
    }
}

data class SampleInsurance(
    val id: String,
    val name: String,
    val type: String,
    val typeName: String,
    val expiryDate: String,
    val daysUntilExpiry: Int,
    val price: Double?,
    val companyName: String?,
    val status: String
)

@Composable
fun InsuranceCard(
    insurance: SampleInsurance,
    onEdit: () -> Unit,
    onRenew: () -> Unit,
    onDelete: () -> Unit
) {
    // Convert string type to enum for shared logic
    val insuranceType = when (insurance.type) {
        "AUTO" -> com.insurancereminder.shared.model.InsuranceType.AUTO
        "MOTORCYCLE" -> com.insurancereminder.shared.model.InsuranceType.MOTORCYCLE
        "HOME" -> com.insurancereminder.shared.model.InsuranceType.HOME
        "HEALTH" -> com.insurancereminder.shared.model.InsuranceType.HEALTH
        "DENTAL" -> com.insurancereminder.shared.model.InsuranceType.DENTAL
        "LIFE" -> com.insurancereminder.shared.model.InsuranceType.LIFE
        "PET" -> com.insurancereminder.shared.model.InsuranceType.PET
        "TRAVEL" -> com.insurancereminder.shared.model.InsuranceType.TRAVEL
        else -> com.insurancereminder.shared.model.InsuranceType.OTHER
    }

    // Use shared theme mapping
    val themeRole = com.insurancereminder.shared.theme.InsuranceThemeMapping.getThemeRole(insuranceType)
    val typeIcon = com.insurancereminder.shared.theme.InsuranceThemeMapping.getIconEmoji(insuranceType)

    val typeIconClass = when (themeRole) {
        com.insurancereminder.shared.theme.InsuranceThemeRole.PRIMARY -> AppStyles.primaryInsurance
        com.insurancereminder.shared.theme.InsuranceThemeRole.SECONDARY -> AppStyles.secondaryInsurance
        com.insurancereminder.shared.theme.InsuranceThemeRole.SUCCESS -> AppStyles.successInsurance
        com.insurancereminder.shared.theme.InsuranceThemeRole.ERROR -> AppStyles.errorInsurance
        com.insurancereminder.shared.theme.InsuranceThemeRole.WARNING -> AppStyles.warningInsurance
        com.insurancereminder.shared.theme.InsuranceThemeRole.INFO -> AppStyles.infoInsurance
        else -> AppStyles.outlineInsurance
    }

    val statusColor = when (insurance.status) {
        "EXPIRED" -> "background-color: #dc3545"
        "EXPIRING_SOON" -> "background-color: #fd7e14"
        else -> "background-color: #28a745"
    }

    Div(
        attrs = {
            classes(AppStyles.insuranceCard)
            onClick { onEdit() }
        }
    ) {
        Div(attrs = { classes(AppStyles.cardHeader) }) {
            Div(attrs = {
                classes(AppStyles.typeIcon, typeIconClass)
            }) {
                Text(typeIcon)
            }

            Span(attrs = {
                classes(AppStyles.statusBadge)
                style {
                    property("background-color", when (insurance.status) {
                        "EXPIRED" -> "var(--color-error, #dc3545)"
                        "EXPIRING_SOON" -> "var(--color-secondary, #fd7e14)"
                        else -> "var(--color-success, #28a745)"
                    })
                }
            }) {
                Text("${insurance.daysUntilExpiry}d")
            }
        }

        Div(attrs = { classes(AppStyles.cardContent) }) {
            H4 { Text(insurance.name) }
            P { Text(insurance.typeName) }
            if (insurance.companyName != null) {
                P { Text(insurance.companyName) }
            }
        }

        Div(attrs = { classes(AppStyles.cardActions) }) {
            insurance.price?.let { price ->
                Span(attrs = { classes(AppStyles.priceText) }) {
                    Text("€${price.toInt()}")
                }
            }

            Div(attrs = { classes(AppStyles.actionButtons) }) {
                Button(
                    attrs = {
                        classes(AppStyles.actionButton, AppStyles.renewButton)
                        onClick {
                            it.stopPropagation()
                            onRenew()
                        }
                    }
                ) {
                    Text("Renovar")
                }

                Button(
                    attrs = {
                        classes(AppStyles.actionButton, AppStyles.deleteButton)
                        onClick {
                            it.stopPropagation()
                            onDelete()
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
fun AddInsuranceView(onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("auto_insurance") }
    var expiryDate by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }

    Div(attrs = { classes(AppStyles.toolbar) }) {
        Button(
            attrs = {
                classes(AppStyles.textButton)
                onClick { onBack() }
            }
        ) {
            Text("← " + LocalizedStrings.get("back"))
        }
        H2 { Text(LocalizedStrings.get("add_insurance")) }
    }

    Div(attrs = { classes(AppStyles.formCard) }) {
        Form {
            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("insurance_name")) }
                Input(type = InputType.Text) {
                    value(name)
                    onInput { name = it.value }
                    classes(AppStyles.formInput)
                    placeholder("ej. Seguro de Auto - Honda Civic")
                }
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("insurance_type")) }
                Select(attrs = {
                    classes(AppStyles.formSelect)
                    onChange { selectedType = it.value }
                }) {
                    Option("auto_insurance") { Text(LocalizedStrings.get("auto_insurance")) }
                    Option("home_insurance") { Text(LocalizedStrings.get("home_insurance")) }
                    Option("health_insurance") { Text(LocalizedStrings.get("health_insurance")) }
                    Option("life_insurance") { Text(LocalizedStrings.get("life_insurance")) }
                    Option("travel_insurance") { Text(LocalizedStrings.get("travel_insurance")) }
                    Option("business_insurance") { Text(LocalizedStrings.get("business_insurance")) }
                    Option("pet_insurance") { Text(LocalizedStrings.get("pet_insurance")) }
                    Option("dental_insurance") { Text(LocalizedStrings.get("dental_insurance")) }
                    Option("vision_insurance") { Text(LocalizedStrings.get("vision_insurance")) }
                    Option("other_insurance") { Text(LocalizedStrings.get("other_insurance")) }
                }
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("expiry_date")) }
                Input(type = InputType.Date) {
                    value(expiryDate)
                    onInput { expiryDate = it.value }
                    classes(AppStyles.formInput)
                }
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("current_price")) }
                Input(type = InputType.Number) {
                    value(price)
                    onInput { price = it.value }
                    classes(AppStyles.formInput)
                    placeholder("0.00")
                }
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("insurance_company")) }
                Input(type = InputType.Text) {
                    value(company)
                    onInput { company = it.value }
                    classes(AppStyles.formInput)
                    placeholder("ej. Mapfre, Sanitas")
                }
            }

            Button(
                attrs = {
                    classes(AppStyles.primaryButton)
                    onClick { onBack() }
                }
            ) {
                Text(LocalizedStrings.get("add_insurance"))
            }
        }
    }
}

@Composable
fun ProfileView(onBack: () -> Unit) {
    Div(attrs = { classes(AppStyles.toolbar) }) {
        Button(
            attrs = {
                classes(AppStyles.textButton)
                onClick { onBack() }
            }
        ) {
            Text("← " + LocalizedStrings.get("back"))
        }
        H2 { Text(LocalizedStrings.get("profile")) }
    }

    Div(attrs = { classes(AppStyles.profileCard) }) {
        H3 { Text("Información de la Cuenta") }
        P { Text("Modo Invitado - Solo Almacenamiento Local") }

        Hr()

        H3 { Text("Comparación de Suscripciones") }

        Div(attrs = { classes(AppStyles.tierComparison) }) {
            Div(attrs = { classes(AppStyles.tierCard) }) {
                H4 { Text(LocalizedStrings.get("free_tier")) }
                Ul {
                    Li { Text("• Solo almacenamiento local") }
                    Li { Text("• Máximo 5 pólizas de seguro") }
                    Li { Text("• Notificaciones básicas") }
                    Li { Text("• Sin subida de archivos") }
                }
            }

            Div(attrs = { classes(AppStyles.tierCard, AppStyles.premiumCard) }) {
                H4 { Text(LocalizedStrings.get("premium_tier")) }
                Ul {
                    Li { Text("• Almacenamiento en la nube") }
                    Li { Text("• Pólizas ilimitadas") }
                    Li { Text("• Subida de archivos PDF") }
                    Li { Text("• Compartir con pareja") }
                }
                Button(attrs = { classes(AppStyles.primaryButton) }) {
                    Text(LocalizedStrings.get("upgrade_to_premium"))
                }
            }
        }
    }
}

class WebStringProvider : StringProvider {
    override fun getString(key: String): String {
        return getLocalizedString(key)
    }

    override fun getString(key: String, vararg args: Any): String {
        val format = getLocalizedString(key)
        return format // Simplified for web
    }

    override fun getCurrentLanguage(): String {
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