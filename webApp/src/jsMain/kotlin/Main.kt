import androidx.compose.runtime.*
import com.insurancereminder.shared.localization.LocalizedStrings
import com.insurancereminder.shared.localization.StringProvider
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

fun main() {
    // Initialize localization
    LocalizedStrings.provider = StringProvider()

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
            property("border-bottom", "0")
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
                Input(type = \"email\") {
                    value(email)
                    onInput { email = it.value }
                    classes(AppStyles.formInput)
                }
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("password")) }
                Input(type = \"password\") {
                    value(password)
                    onInput { password = it.value }
                    classes(AppStyles.formInput)
                }
            }

            if (!isLoginMode) {
                Div(attrs = { classes(AppStyles.formGroup) }) {
                    Label { Text(LocalizedStrings.get("confirm_password")) }
                    Input(type = \"password\") {
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
    var showEditDialog by remember { mutableStateOf(false) }
    var insuranceToEdit by remember { mutableStateOf<SampleInsurance?>(null) }
    var showRenewDialog by remember { mutableStateOf(false) }
    var insuranceToRenew by remember { mutableStateOf<SampleInsurance?>(null) }

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
            ),
            SampleInsurance(
                id = "4",
                name = "BMW X3 2021",
                type = "AUTO",
                typeName = "Seguro de Auto",
                expiryDate = "2024-05-20",
                daysUntilExpiry = 90,
                price = 1250.0,
                companyName = "Genesis Seguros",
                status = "ACTIVE"
            ),
            SampleInsurance(
                id = "5",
                name = "Yamaha MT-07",
                type = "MOTORCYCLE",
                typeName = "Seguro de Moto",
                expiryDate = "2024-04-08",
                daysUntilExpiry = 68,
                price = 380.0,
                companyName = "Balumba",
                status = "ACTIVE"
            ),
            SampleInsurance(
                id = "6",
                name = "Seguro Dental Premium",
                type = "DENTAL",
                typeName = "Seguro Dental",
                expiryDate = "2024-12-15",
                daysUntilExpiry = 285,
                price = 480.0,
                companyName = "Qualitas",
                status = "ACTIVE"
            ),
            SampleInsurance(
                id = "7",
                name = "Apartamento Sevilla",
                type = "HOME",
                typeName = "Seguro de Hogar",
                expiryDate = "2024-01-30",
                daysUntilExpiry = 0,
                price = 280.0,
                companyName = "Direct Seguros",
                status = "EXPIRED"
            ),
            SampleInsurance(
                id = "8",
                name = "Seguro de Vida",
                type = "LIFE",
                typeName = "Seguro de Vida",
                expiryDate = "2024-09-22",
                daysUntilExpiry = 205,
                price = 650.0,
                companyName = "Mutua Madrileña",
                status = "ACTIVE"
            ),
            SampleInsurance(
                id = "9",
                name = "Viaje Europa 2024",
                type = "TRAVEL",
                typeName = "Seguro de Viaje",
                expiryDate = "2024-03-01",
                daysUntilExpiry = 31,
                price = 45.0,
                companyName = "Pelayo",
                status = "EXPIRING_SOON"
            ),
            SampleInsurance(
                id = "10",
                name = "Perro Golden Retriever",
                type = "PET",
                typeName = "Seguro de Mascotas",
                expiryDate = "2024-07-12",
                daysUntilExpiry = 152,
                price = 320.0,
                companyName = "Reale Seguros",
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
                            onEdit = {
                                insuranceToEdit = insurance
                                showEditDialog = true
                            },
                            onRenew = {
                                insuranceToRenew = insurance
                                showRenewDialog = true
                            },
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

    // Edit insurance dialog
    if (showEditDialog && insuranceToEdit != null) {
        EditInsuranceDialog(
            insurance = insuranceToEdit!!,
            onSave = { updatedInsurance ->
                sampleInsurances = sampleInsurances.map { insurance ->
                    if (insurance.id == updatedInsurance.id) updatedInsurance else insurance
                }
                showEditDialog = false
                insuranceToEdit = null
            },
            onCancel = {
                showEditDialog = false
                insuranceToEdit = null
            }
        )
    }

    // Renew insurance dialog
    if (showRenewDialog && insuranceToRenew != null) {
        RenewInsuranceDialog(
            insurance = insuranceToRenew!!,
            onRenew = { renewedInsurance ->
                sampleInsurances = sampleInsurances.map { insurance ->
                    if (insurance.id == renewedInsurance.id) renewedInsurance else insurance
                }
                showRenewDialog = false
                insuranceToRenew = null
            },
            onCancel = {
                showRenewDialog = false
                insuranceToRenew = null
            }
        )
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
                property("z-index", "1000")
            }
        }) {
            Div(attrs = {
                style {
                    backgroundColor(Color("var(--color-surface)"))
                    padding(24.px)
                    borderRadius(12.px)
                    maxWidth(400.px)
                    margin(20.px)
                    property("box-shadow", "0 4px 16px rgba(0, 0, 0, 0.2)")
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
                Input(type = \"text\") {
                    value(name)
                    onInput { name = it.value }
                    classes(AppStyles.formInput)
                    attr("placeholder","ej. Seguro de Auto - Honda Civic")
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
                Input(type = \"date\") {
                    value(expiryDate)
                    onInput { expiryDate = it.value }
                    classes(AppStyles.formInput)
                }
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("current_price")) }
                Input(type = \"number\") {
                    value(price)
                    onInput { price = it.value }
                    classes(AppStyles.formInput)
                    attr("placeholder","0.00")
                }
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text(LocalizedStrings.get("insurance_company")) }
                Input(type = \"text\") {
                    value(company)
                    onInput { company = it.value }
                    classes(AppStyles.formInput)
                    attr("placeholder","ej. Mapfre, Sanitas")
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


@Composable
fun EditInsuranceDialog(
    insurance: SampleInsurance,
    onSave: (SampleInsurance) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(insurance.name) }
    var selectedType by remember { mutableStateOf(insurance.type) }
    var expiryDate by remember { mutableStateOf(insurance.expiryDate) }
    var price by remember { mutableStateOf(insurance.price?.toString() ?: "") }
    var company by remember { mutableStateOf(insurance.companyName ?: "") }

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
            property("z-index", "1000")
        }
    }) {
        Div(attrs = {
            style {
                backgroundColor(Color("var(--color-surface)"))
                padding(24.px)
                borderRadius(12.px)
                maxWidth(500.px)
                maxHeight(80.percent)
                margin(20.px)
                property("box-shadow", "0 4px 16px rgba(0, 0, 0, 0.2)")
                overflowY("auto")
            }
        }) {
            H3 { Text("Edit Insurance") }

            Form {
                Div(attrs = { classes(AppStyles.formGroup) }) {
                    Label { Text(LocalizedStrings.get("insurance_name")) }
                    Input(type = \"text\") {
                        value(name)
                        onInput { name = it.value }
                        classes(AppStyles.formInput)
                    }
                }

                Div(attrs = { classes(AppStyles.formGroup) }) {
                    Label { Text(LocalizedStrings.get("insurance_type")) }
                    Select(attrs = {
                        classes(AppStyles.formSelect)
                        onChange { selectedType = it.value }
                    }) {
                        Option("AUTO") { Text(LocalizedStrings.get("auto_insurance")) }
                        Option("MOTORCYCLE") { Text("Motorcycle Insurance") }
                        Option("HOME") { Text(LocalizedStrings.get("home_insurance")) }
                        Option("HEALTH") { Text(LocalizedStrings.get("health_insurance")) }
                        Option("DENTAL") { Text(LocalizedStrings.get("dental_insurance")) }
                        Option("LIFE") { Text(LocalizedStrings.get("life_insurance")) }
                        Option("PET") { Text(LocalizedStrings.get("pet_insurance")) }
                        Option("TRAVEL") { Text(LocalizedStrings.get("travel_insurance")) }
                        Option("OTHER") { Text(LocalizedStrings.get("other_insurance")) }
                    }
                }

                Div(attrs = { classes(AppStyles.formGroup) }) {
                    Label { Text(LocalizedStrings.get("expiry_date")) }
                    Input(type = \"date\") {
                        value(expiryDate)
                        onInput { expiryDate = it.value }
                        classes(AppStyles.formInput)
                    }
                }

                Div(attrs = { classes(AppStyles.formGroup) }) {
                    Label { Text(LocalizedStrings.get("current_price")) }
                    Input(type = \"number\") {
                        value(price)
                        onInput { price = it.value }
                        classes(AppStyles.formInput)
                        attr("placeholder","0.00")
                    }
                }

                Div(attrs = { classes(AppStyles.formGroup) }) {
                    Label { Text(LocalizedStrings.get("insurance_company")) }
                    Input(type = \"text\") {
                        value(company)
                        onInput { company = it.value }
                        classes(AppStyles.formInput)
                    }
                }

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
                        onClick { onCancel() }
                    }) { Text("Cancel") }

                    Button(attrs = {
                        classes(AppStyles.primaryButton)
                        onClick {
                            val updatedInsurance = insurance.copy(
                                name = name,
                                type = selectedType,
                                expiryDate = expiryDate,
                                price = price.toDoubleOrNull(),
                                companyName = company.takeIf { it.isNotBlank() }
                            )
                            onSave(updatedInsurance)
                        }
                    }) { Text("Save Changes") }
                }
            }
        }
    }
}

@Composable
fun RenewInsuranceDialog(
    insurance: SampleInsurance,
    onRenew: (SampleInsurance) -> Unit,
    onCancel: () -> Unit
) {
    var newExpiryDate by remember {
        // Default to one year from current expiry
        val currentDate = insurance.expiryDate
        val oneYearLater = try {
            val parts = currentDate.split("-")
            val year = parts[0].toInt() + 1
            "$year-${parts[1]}-${parts[2]}"
        } catch (e: Exception) {
            val today = js("new Date()")
            val nextYear = js("new Date(today.getFullYear() + 1, today.getMonth(), today.getDate())")
            "${js("nextYear.getFullYear()")}-${String.format("%02d", js("nextYear.getMonth() + 1").toString().toInt())}-${String.format("%02d", js("nextYear.getDate()").toString().toInt())}"
        }
        mutableStateOf(oneYearLater)
    }

    var newPrice by remember { mutableStateOf(insurance.price?.toString() ?: "") }

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
            property("z-index", "1000")
        }
    }) {
        Div(attrs = {
            style {
                backgroundColor(Color("var(--color-surface)"))
                padding(24.px)
                borderRadius(12.px)
                maxWidth(400.px)
                margin(20.px)
                property("box-shadow", "0 4px 16px rgba(0, 0, 0, 0.2)")
            }
        }) {
            H3 { Text("Renew Insurance") }

            Div(attrs = {
                style {
                    backgroundColor(Color("var(--color-surface-variant, #f5f5f5)"))
                    padding(16.px)
                    borderRadius(8.px)
                    marginBottom(20.px)
                }
            }) {
                P(attrs = {
                    style {
                        margin(0.px)
                        fontWeight("bold")
                    }
                }) { Text(insurance.name) }
                P(attrs = {
                    style {
                        margin(4.px, 0.px, 0.px, 0.px)
                        fontSize(14.px)
                        color(Color("var(--color-on-surface-variant, #666)"))
                    }
                }) { Text("Current expiry: ${insurance.expiryDate}") }
            }

            // +1 Year button
            Button(attrs = {
                onClick {
                    val currentDate = insurance.expiryDate
                    val oneYearLater = try {
                        val parts = currentDate.split("-")
                        val year = parts[0].toInt() + 1
                        "$year-${parts[1]}-${parts[2]}"
                    } catch (e: Exception) {
                        "2025-12-31"
                    }
                    newExpiryDate = oneYearLater
                }
                style {
                    width(100.percent)
                    padding(12.px)
                    marginBottom(16.px)
                    backgroundColor(Color("var(--color-primary-container, #e3f2fd)"))
                    color(Color("var(--color-on-primary-container, #1976d2)"))
                    border(1.px, LineStyle.Solid, Color("var(--color-primary, #1976d2)"))
                    borderRadius(8.px)
                    cursor("pointer")
                }
            }) {
                Text("+ Add 1 Year")
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text("New Expiry Date") }
                Input(type = \"date\") {
                    value(newExpiryDate)
                    onInput { newExpiryDate = it.value }
                    classes(AppStyles.formInput)
                }
            }

            Div(attrs = { classes(AppStyles.formGroup) }) {
                Label { Text("Update Price (Optional)") }
                Input(type = \"number\") {
                    value(newPrice)
                    onInput { newPrice = it.value }
                    classes(AppStyles.formInput)
                    attr("placeholder", "0.00")
                    attr("step", "0.01")
                }
            }

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
                    onClick { onCancel() }
                }) { Text("Cancel") }

                Button(attrs = {
                    classes(AppStyles.primaryButton)
                    onClick {
                        val renewedInsurance = insurance.copy(
                            expiryDate = newExpiryDate,
                            price = newPrice.toDoubleOrNull() ?: insurance.price,
                            status = "ACTIVE" // Reset status when renewed
                        )
                        onRenew(renewedInsurance)
                    }
                }) { Text("Renew") }
            }
        }
    }
}