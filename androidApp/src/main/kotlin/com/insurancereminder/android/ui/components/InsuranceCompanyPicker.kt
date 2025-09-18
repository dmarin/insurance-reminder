package com.insurancereminder.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import com.insurancereminder.shared.model.InsuranceCompany
import com.insurancereminder.shared.model.InsuranceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceCompanyPicker(
    selectedCompany: InsuranceCompany?,
    companies: List<InsuranceCompany>,
    insuranceType: InsuranceType,
    onCompanySelected: (InsuranceCompany?) -> Unit,
    onOtherSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCompany?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Insurance Company") },
            placeholder = { Text("Select a company") },
            leadingIcon = {
                if (selectedCompany != null && selectedCompany.id != "other") {
                    CompanyLogo(
                        logoUrl = selectedCompany.logoUrl,
                        companyName = selectedCompany.displayName,
                        size = 24.dp
                    )
                } else {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            // Filter companies by insurance type
            val filteredCompanies = companies.filter { company ->
                company.supportedTypes.contains(insuranceType)
            }

            // Show filtered companies
            filteredCompanies.forEach { company ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CompanyLogo(
                                logoUrl = company.logoUrl,
                                companyName = company.displayName,
                                size = 32.dp
                            )
                            Text(
                                text = company.displayName,
                                fontWeight = if (selectedCompany?.id == company.id) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (selectedCompany?.id == company.id) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onCompanySelected(company)
                        isExpanded = false
                    }
                )
            }

            // Divider and "Other" option
            if (filteredCompanies.isNotEmpty()) {
                HorizontalDivider()
            }

            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Other",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(8.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Other / Add Company",
                            fontWeight = if (selectedCompany?.id == "other") FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (selectedCompany?.id == "other") {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                onClick = {
                    onOtherSelected()
                    isExpanded = false
                }
            )
        }
    }
}

@Composable
fun CompanyLogo(
    logoUrl: String,
    companyName: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    if (logoUrl.isNotBlank()) {
        Image(
            painter = rememberAsyncImagePainter(logoUrl),
            contentDescription = "$companyName logo",
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Surface(
            modifier = modifier.size(size),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                Icons.Default.Business,
                contentDescription = companyName,
                modifier = Modifier
                    .size(size * 0.6f)
                    .padding(size * 0.2f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}