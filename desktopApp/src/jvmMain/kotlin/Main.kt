import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.insurancereminder.shared.localization.LocalizedStrings
import com.insurancereminder.shared.localization.StringProvider
import com.insurancereminder.shared.model.Insurance
import com.insurancereminder.shared.model.InsuranceType
import kotlinx.datetime.LocalDate
import java.util.*

fun main() = application {
    // Initialize localization
    LocalizedStrings.provider = DesktopStringProvider()

    Window(
        onCloseRequest = ::exitApplication,
        title = LocalizedStrings.get("app_name")
    ) {
        InsuranceReminderDesktopApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceReminderDesktopApp() {
    var currentView by remember { mutableStateOf("insurances") }
    var insurances by remember { mutableStateOf(listOf<Insurance>()) }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar Navigation
            NavigationRail(
                modifier = Modifier.fillMaxHeight().width(80.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                NavigationRailItem(
                    selected = currentView == "insurances",
                    onClick = { currentView = "insurances" },
                    icon = { Icon(Icons.Default.Shield, contentDescription = null) },
                    label = { Text("Seguros") }
                )

                NavigationRailItem(
                    selected = currentView == "add",
                    onClick = { currentView = "add" },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    label = { Text("Añadir") }
                )

                NavigationRailItem(
                    selected = currentView == "profile",
                    onClick = { currentView = "profile" },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") }
                )
            }

            // Main Content
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp)
            ) {
                when (currentView) {
                    "insurances" -> InsuranceListView(
                        insurances = insurances,
                        onAddInsurance = { currentView = "add" }
                    )
                    "add" -> AddInsuranceView(
                        onBack = { currentView = "insurances" },
                        onSave = { insurance ->
                            insurances = insurances + insurance
                            currentView = "insurances"
                        }
                    )
                    "profile" -> ProfileView()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceListView(
    insurances: List<Insurance>,
    onAddInsurance: () -> Unit
) {
    Column {
        TopAppBar(
            title = { Text(LocalizedStrings.get("insurance_reminders")) },
            actions = {
                FilledTonalButton(onClick = onAddInsurance) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(LocalizedStrings.get("add_insurance"))
                }
            }
        )

        if (insurances.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No hay seguros añadidos",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "Haz clic en 'Añadir Seguro' para comenzar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(insurances) { insurance ->
                    InsuranceCard(insurance = insurance)
                }
            }
        }
    }
}

@Composable
fun InsuranceCard(insurance: Insurance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = insurance.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = insurance.type.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    insurance.companyName?.let { company ->
                        Text(
                            text = company,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    insurance.currentPrice?.let { price ->
                        Text(
                            text = "€${String.format("%.2f", price)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (insurance.policyFileUrl != null) {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = "Has attachment",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Expira: ${insurance.expiryDate}",
                style = MaterialTheme.typography.bodySmall
            )

            insurance.policyNumber?.let { policyNum ->
                Text(
                    text = "Póliza: $policyNum",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddInsuranceView(
    onBack: () -> Unit,
    onSave: (Insurance) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(InsuranceType.AUTO) }
    var expiryDate by remember { mutableStateOf("2024-12-31") }
    var price by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var policyNumber by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
            }
            Text(
                LocalizedStrings.get("add_insurance"),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(LocalizedStrings.get("insurance_name")) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text(LocalizedStrings.get("insurance_company")) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(LocalizedStrings.get("current_price")) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it },
                label = { Text(LocalizedStrings.get("expiry_date")) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = policyNumber,
                onValueChange = { policyNumber = it },
                label = { Text("Número de Póliza") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val insurance = Insurance(
                        id = "desktop_${System.currentTimeMillis()}",
                        name = name,
                        type = selectedType,
                        expiryDate = LocalDate.parse(expiryDate),
                        currentPrice = price.toDoubleOrNull(),
                        companyName = company.takeIf { it.isNotBlank() },
                        policyNumber = policyNumber.takeIf { it.isNotBlank() }
                    )
                    onSave(insurance)
                },
                enabled = name.isNotBlank() && expiryDate.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(LocalizedStrings.get("add_insurance"))
            }
        }
    }
}

@Composable
fun ProfileView() {
    Column {
        Text(
            LocalizedStrings.get("profile"),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Información de la Cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aplicación de Escritorio - Almacenamiento Local")

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Funciones de la Aplicación de Escritorio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Almacenamiento local seguro")
                Text("• Interfaz optimizada para escritorio")
                Text("• Sincronización futura con la nube")
                Text("• Notificaciones del sistema")
            }
        }
    }
}

class DesktopStringProvider : StringProvider {
    override fun getString(key: String): String {
        val locale = Locale.getDefault()
        return when (locale.language) {
            "es" -> getSpanishString(key)
            else -> getEnglishString(key)
        }
    }

    override fun getString(key: String, vararg args: Any): String {
        return String.format(getString(key), *args)
    }

    override fun getCurrentLanguage(): String {
        return Locale.getDefault().language
    }

    private fun getSpanishString(key: String): String {
        return when (key) {
            "app_name" -> "Recordatorio de Seguros"
            "insurance_reminders" -> "Recordatorios de Seguros"
            "add_insurance" -> "Añadir Seguro"
            "insurance_name" -> "Nombre del Seguro"
            "insurance_company" -> "Compañía de Seguros"
            "current_price" -> "Precio Actual (€)"
            "expiry_date" -> "Fecha de Expiración"
            "profile" -> "Perfil"
            else -> key
        }
    }

    private fun getEnglishString(key: String): String {
        return when (key) {
            "app_name" -> "Insurance Reminder"
            "insurance_reminders" -> "Insurance Reminders"
            "add_insurance" -> "Add Insurance"
            "insurance_name" -> "Insurance Name"
            "insurance_company" -> "Insurance Company"
            "current_price" -> "Current Price (€)"
            "expiry_date" -> "Expiry Date"
            "profile" -> "Profile"
            else -> key
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    InsuranceReminderDesktopApp()
}