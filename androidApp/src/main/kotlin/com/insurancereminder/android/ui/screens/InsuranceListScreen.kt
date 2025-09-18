package com.insurancereminder.android.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import coil.compose.rememberAsyncImagePainter
import com.insurancereminder.android.viewmodel.AuthViewModel
import com.insurancereminder.android.viewmodel.InsuranceViewModel
import com.insurancereminder.android.ui.utils.FormFactor
import com.insurancereminder.android.ui.utils.getFormFactor
import com.insurancereminder.android.ui.utils.getColumnsForFormFactor
import com.insurancereminder.shared.model.Insurance
import com.insurancereminder.shared.model.InsuranceType
import com.insurancereminder.shared.presentation.groupByCategory
import com.insurancereminder.shared.utils.*
import com.insurancereminder.android.ui.theme.getInsuranceTypeColor
import com.insurancereminder.shared.ui.components.DatePickerUtils
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate as JavaLocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// Extension functions are now defined in shared code

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceListScreen(
    viewModel: InsuranceViewModel,
    authViewModel: AuthViewModel,
    windowSizeClass: WindowSizeClass,
    onAddInsurance: () -> Unit,
    onEditInsurance: (Insurance) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val insurances by viewModel.insurances.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val formFactor = getFormFactor(windowSizeClass)
    val columns = getColumnsForFormFactor(formFactor)

    var showDeleteDialog by remember { mutableStateOf<Insurance?>(null) }
    var showRenewDialog by remember { mutableStateOf<Insurance?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insurance Reminders") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = WindowInsets(0, 0, 0, 0) // Remove insets for edge-to-edge
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddInsurance
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Insurance")
            }
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            authState.error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (insurances.isEmpty()) {
                EmptyState(onAddInsurance = onAddInsurance)
            } else {
                InsuranceListContent(
                    insurances = insurances,
                    columns = columns,
                    onEditInsurance = onEditInsurance,
                    onDeleteInsurance = { showDeleteDialog = it },
                    onRenewInsurance = { showRenewDialog = it }
                )
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { insurance ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Insurance") },
            text = { Text("Are you sure you want to delete ${insurance.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteInsurance(insurance.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Renew dialog
    showRenewDialog?.let { insurance ->
        RenewInsuranceDialog(
            insurance = insurance,
            onDismiss = { showRenewDialog = null },
            onRenew = { newDate ->
                // Update insurance with new expiry date
                viewModel.renewInsurance(insurance.id, newDate)
                showRenewDialog = null
            }
        )
    }
}

@Composable
private fun EmptyState(onAddInsurance: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No insurances yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add your first insurance policy to get started with tracking and reminders",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAddInsurance,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add First Insurance")
        }
    }
}

@Composable
private fun InsuranceListContent(
    insurances: List<Insurance>,
    columns: Int,
    onEditInsurance: (Insurance) -> Unit,
    onDeleteInsurance: (Insurance) -> Unit,
    onRenewInsurance: (Insurance) -> Unit
) {
    // Group insurances by category using shared logic
    val groupedInsurances = insurances.groupByCategory()

    if (columns == 1) {
        // Single column layout for phones
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedInsurances.forEach { group ->
                item {
                    CategoryHeader(category = group.category)
                }

                items(group.insurances) { insurance ->
                    SwipeableInsuranceCard(
                        insurance = insurance,
                        onEdit = { onEditInsurance(insurance) },
                        onDelete = { onDeleteInsurance(insurance) },
                        onRenew = { onRenewInsurance(insurance) }
                    )
                }
            }
        }
    } else {
        // Grid layout for tablets and foldables
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedInsurances.forEach { group ->
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    CategoryHeader(category = group.category)
                }
                items(group.insurances) { insurance ->
                    InsuranceCard(
                        insurance = insurance,
                        onClick = { onEditInsurance(insurance) },
                        onDelete = { onDeleteInsurance(insurance) },
                        onRenew = { onRenewInsurance(insurance) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InsuranceCard(
    insurance: Insurance,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRenew: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = getInsuranceTypeColor(insurance.type),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getInsuranceTypeIcon(insurance.type),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "${insurance.daysUntilExpiry}d",
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        insurance.isExpired -> MaterialTheme.colorScheme.error
                        insurance.isExpiringSoon -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = insurance.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = insurance.type.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            insurance.companyName?.let { companyName ->
                Text(
                    text = companyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (insurance.currentPrice != null) {
                    Text(
                        text = "€${insurance.currentPrice}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(onClick = onRenew) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Renew",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableInsuranceCard(
    insurance: Insurance,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRenew: () -> Unit
) {
    var swipeOffset by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 80.dp.toPx() }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Background actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Renew action (left swipe reveals this on the right)
            if (swipeOffset < -swipeThreshold) {
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onRenew() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Renew",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                "Renew",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Delete action (right swipe reveals this on the left)
            if (swipeOffset > swipeThreshold) {
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onError
                            )
                            Text(
                                "Delete",
                                color = MaterialTheme.colorScheme.onError,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Main card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(swipeOffset.roundToInt(), 0) }
                .clickable { onEdit() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            EnhancedInsuranceCard(insurance = insurance)
        }
    }
}

@Composable
private fun EnhancedInsuranceCard(insurance: Insurance) {
    val daysUntilExpiry = insurance.daysUntilExpiry
    val isExpiringSoon = insurance.isExpiringSoon
    val isExpired = insurance.isExpired

    // Background based on insurance type
    val backgroundImage = getInsuranceTypeBackgroundImage(insurance.type)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        // Background image
        Image(
            painter = rememberAsyncImagePainter(backgroundImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        // Overlay gradient for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Insurance type icon and company logo
                Box(
                    modifier = Modifier.size(48.dp)
                ) {
                    // Type icon background
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = getInsuranceTypeColor(insurance.type)
                    ) {
                        Icon(
                            imageVector = getInsuranceTypeIcon(insurance.type),
                            contentDescription = insurance.type.displayName,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(10.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Company logo overlay
                    insurance.companyLogoUrl?.let { logoUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(logoUrl),
                            contentDescription = "${insurance.companyName} logo",
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .align(Alignment.BottomEnd),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = insurance.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Price
                insurance.currentPrice?.let { price ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Euro,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = String.format("%.0f", price),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Status badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        isExpired -> MaterialTheme.colorScheme.error
                        isExpiringSoon -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                ) {
                    Text(
                        text = when {
                            isExpired -> "EXPIRED"
                            isExpiringSoon -> "${daysUntilExpiry}d"
                            else -> "${daysUntilExpiry}d"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            isExpired -> MaterialTheme.colorScheme.onError
                            isExpiringSoon -> MaterialTheme.colorScheme.onTertiary
                            else -> MaterialTheme.colorScheme.onPrimary
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

                // Attachment indicator
                if (insurance.policyFileUrl != null) {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = "Has attachment",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun RenewInsuranceDialog(
    insurance: Insurance,
    onDismiss: () -> Unit,
    onRenew: (LocalDate) -> Unit
) {
    var newExpiryDate by remember { mutableStateOf(insurance.expiryDate.toString()) }
    var pickedDate by remember { mutableStateOf(insurance.expiryDate.toJavaLocalDate()) }
    val dateDialogState = rememberMaterialDialogState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Renew ${insurance.name}") },
        text = {
            Column {
                Text("Select new expiry date:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newExpiryDate,
                    onValueChange = { newExpiryDate = it },
                    label = { Text("Expiry Date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    trailingIcon = {
                        IconButton(onClick = { dateDialogState.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                        }
                    },
                    isError = !DatePickerUtils.isValidDateFormat(newExpiryDate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { dateDialogState.show() },
                    readOnly = false
                )

                if (!DatePickerUtils.isValidDateFormat(newExpiryDate)) {
                    Text(
                        text = "Invalid date format. Use YYYY-MM-DD",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    DatePickerUtils.parseDate(newExpiryDate)?.let { date ->
                        onRenew(date)
                    }
                    onDismiss()
                },
                enabled = DatePickerUtils.isValidDateFormat(newExpiryDate)
            ) {
                Text("Renew")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Date picker dialog
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "OK") {
                newExpiryDate = pickedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = pickedDate,
            title = "Pick new expiry date",
            allowedDateValidator = { date ->
                // Allow future dates (after current expiry date makes sense for renewal)
                !date.isBefore(JavaLocalDate.now())
            }
        ) { date ->
            pickedDate = date
        }
    }
}

// Helper functions using shared utilities
private fun getInsuranceTypeIcon(type: InsuranceType): ImageVector {
    return when (type) {
        InsuranceType.AUTO -> Icons.Default.DirectionsCar
        InsuranceType.MOTORCYCLE -> Icons.Default.DirectionsBike
        InsuranceType.HOME -> Icons.Default.Home
        InsuranceType.HEALTH -> Icons.Default.LocalHospital
        InsuranceType.DENTAL -> Icons.Default.LocalHospital
        InsuranceType.LIFE -> Icons.Default.Favorite
        InsuranceType.PET -> Icons.Default.Pets
        InsuranceType.TRAVEL -> Icons.Default.Flight
        InsuranceType.OTHER -> Icons.Default.Category
    }
}

// Removed - now using theme function

private fun getInsuranceTypeBackgroundImage(type: InsuranceType): String {
    return InsuranceUtils.getInsuranceTypeBackgroundImage(type)
}