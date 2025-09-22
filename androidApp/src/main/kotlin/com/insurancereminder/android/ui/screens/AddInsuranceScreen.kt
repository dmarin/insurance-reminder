package com.insurancereminder.android.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.insurancereminder.android.ui.components.InsuranceCompanyPicker
import com.insurancereminder.android.viewmodel.InsuranceViewModel
import com.insurancereminder.shared.di.SharedModule
import com.insurancereminder.shared.model.ComparisonProvider
import com.insurancereminder.shared.model.InsuranceCompany
import com.insurancereminder.shared.model.Insurance
import com.insurancereminder.shared.model.InsuranceType
import com.insurancereminder.shared.service.ComparisonService
import com.insurancereminder.shared.service.InsuranceCompanyService
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import com.insurancereminder.shared.ui.components.DatePickerUtils
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate as JavaLocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInsuranceScreen(
    viewModel: InsuranceViewModel,
    insurance: Insurance? = null, // null for add, Insurance object for edit
    onNavigateBack: () -> Unit
) {
    val isEditing = insurance != null
    var name by remember { mutableStateOf(insurance?.name ?: "") }
    var selectedType by remember { mutableStateOf(insurance?.type ?: InsuranceType.AUTO) }
    var expiryDate by remember { mutableStateOf(insurance?.expiryDate?.toString() ?: "") }
    var pickedDate by remember { mutableStateOf(insurance?.expiryDate?.let { it.toJavaLocalDate() } ?: JavaLocalDate.now()) }
    val dateDialogState = rememberMaterialDialogState()
    var reminderDays by remember { mutableStateOf(insurance?.reminderDaysBefore?.toString() ?: "30") }
    var currentPrice by remember { mutableStateOf(insurance?.currentPrice?.toString() ?: "") }
    var companyName by remember { mutableStateOf(insurance?.companyName ?: "") }
    var policyNumber by remember { mutableStateOf(insurance?.policyNumber ?: "") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isTypeDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCompany by remember { mutableStateOf<InsuranceCompany?>(null) }

    val context = LocalContext.current
    val comparisonService = remember { ComparisonService() }
    val sharedModule = remember { SharedModule() }
    val insuranceCompanyService = remember { sharedModule.provideInsuranceCompanyService() }

    val availableCompanies by insuranceCompanyService.getCompaniesByCountryAndType("ES", selectedType).collectAsState(initial = emptyList())

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        uri?.let {
            val cursor = context.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIndex = c.getColumnIndex("_display_name")
                    if (nameIndex != -1) {
                        selectedFileName = c.getString(nameIndex)
                    }
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Insurance" else "Add Insurance") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = WindowInsets.statusBars
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Insurance Name") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., Auto Insurance - Honda Civic") }
            )

            ExposedDropdownMenuBox(
                expanded = isTypeDropdownExpanded,
                onExpandedChange = { isTypeDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Insurance Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isTypeDropdownExpanded,
                    onDismissRequest = { isTypeDropdownExpanded = false }
                ) {
                    InsuranceType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                selectedType = type
                                isTypeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Date picker field with validation
            OutlinedTextField(
                value = if (expiryDate.isNotBlank()) expiryDate else pickedDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                onValueChange = { expiryDate = it },
                label = { Text("Expiry Date") },
                placeholder = { Text("YYYY-MM-DD") },
                trailingIcon = {
                    IconButton(onClick = { dateDialogState.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                },
                isError = expiryDate.isNotBlank() && !DatePickerUtils.isValidDateFormat(expiryDate),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { dateDialogState.show() },
                readOnly = false
            )

            // Show error message for invalid date format
            if (expiryDate.isNotBlank() && !DatePickerUtils.isValidDateFormat(expiryDate)) {
                Text(
                    text = "Invalid date format. Use YYYY-MM-DD",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            OutlinedTextField(
                value = reminderDays,
                onValueChange = { reminderDays = it },
                label = { Text("Reminder Days Before") },
                placeholder = { Text("30") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = currentPrice,
                onValueChange = { currentPrice = it },
                label = { Text("Current Price (€)") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth()
            )

            InsuranceCompanyPicker(
                selectedCompany = selectedCompany,
                companies = availableCompanies,
                insuranceType = selectedType,
                onCompanySelected = { company ->
                    selectedCompany = company
                    companyName = company?.displayName ?: ""
                },
                onOtherSelected = {
                    selectedCompany = InsuranceCompanyService.getOtherCompany()
                    companyName = ""
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Show manual company name input when "Other" is selected
            if (selectedCompany?.id == "other") {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company Name") },
                    placeholder = { Text("Enter company name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = policyNumber,
                onValueChange = { policyNumber = it },
                label = { Text("Policy Number") },
                placeholder = { Text("Optional") },
                modifier = Modifier.fillMaxWidth()
            )

            // Image section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Insurance Photo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Add a photo of the item you're insuring or company logo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (selectedImageUri != null) {
                        // Display selected image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Selected insurance image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Remove button
                            IconButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Remove image",
                                    tint = Color.White
                                )
                            }
                        }
                    } else {
                        // Image picker placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tap to add photo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    if (selectedImageUri == null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gallery")
                            }
                        }
                    }
                }
            }

            // File upload section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Policy Document",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("application/pdf") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select PDF")
                        }
                    }

                    selectedFileName?.let { fileName ->
                        Text(
                            text = "Selected: $fileName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Comparison providers section
            if (comparisonService.getAvailableProviders(selectedType).isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Compare Prices",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Find better deals before your policy expires",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(comparisonService.getAvailableProviders(selectedType)) { provider ->
                                OutlinedButton(
                                    onClick = {
                                        // This would open a web browser
                                        // Implementation would use Intent with ACTION_VIEW
                                    }
                                ) {
                                    Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(provider.displayName)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val finalDate = if (expiryDate.isNotBlank()) {
                            expiryDate
                        } else {
                            pickedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        }
                        val date = LocalDate.parse(finalDate)
                        val company = selectedCompany
                        val finalCompanyName = when {
                            company?.id == "other" -> companyName.takeIf { it.isNotBlank() }
                            company != null -> company.displayName
                            else -> companyName.takeIf { it.isNotBlank() }
                        }

                        viewModel.addInsurance(
                            name = name,
                            type = selectedType,
                            expiryDate = date,
                            reminderDaysBefore = reminderDays.toIntOrNull() ?: 30,
                            currentPrice = currentPrice.toDoubleOrNull(),
                            companyName = finalCompanyName,
                            companyId = company?.id?.takeIf { it != "other" },
                            companyLogoUrl = company?.logoUrl?.takeIf { it.isNotBlank() && company.id != "other" },
                            policyNumber = policyNumber.takeIf { it.isNotBlank() },
                            onSuccess = { onNavigateBack() }
                        )
                    } catch (e: Exception) {
                        // Handle invalid date format
                    }
                },
                enabled = !isLoading && name.isNotBlank() &&
                    (expiryDate.isNotBlank() && DatePickerUtils.isValidDateFormat(expiryDate)) || expiryDate.isBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEditing) "Save Changes" else "Add Insurance")
                }
            }
        }
    }

    // Enhanced date picker dialog with validation
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "OK") {
                expiryDate = pickedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = pickedDate,
            title = "Pick expiry date",
            allowedDateValidator = { date ->
                // Allow future dates and today
                !date.isBefore(JavaLocalDate.now())
            }
        ) { date ->
            pickedDate = date
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && isEditing && insurance != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Insurance") },
            text = { Text("Are you sure you want to delete ${insurance.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteInsurance(insurance.id)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}