package com.insurancereminder.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.insurancereminder.android.viewmodel.AuthViewModel
import com.insurancereminder.shared.model.SubscriptionTier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    var partnerEmail by remember { mutableStateOf("") }
    var showPartnerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Subscription") }
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
            // User Info Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Account Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    authState.currentUser?.let { user ->
                        Text("Email: ${user.email}")
                        Text("Plan: ${authState.subscriptionDisplayName}")

                        if (authState.canAccessPremiumFeatures) {
                            Text(
                                text = "✓ Premium Features Active",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } ?: Column {
                        Text("Plan: ${authState.subscriptionDisplayName}")
                        Text("Limited to 5 policies • Local storage only")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { authViewModel.exitGuestMode() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign Up for Premium")
                        }
                        Text(
                            text = "Get unlimited policies, cloud sync & sharing",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Subscription Features Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Plan Comparison",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Free Tier (Guest Mode)
                    Column {
                        Text(
                            text = "Free (Guest Mode)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("• Local storage only")
                        Text("• Maximum 5 insurance policies")
                        Text("• Basic notifications")
                        Text("• No file uploads")
                        Text("• No sharing features")
                        Text("• Data stays on device")
                    }

                    HorizontalDivider()

                    // Premium Tier (Any Registered User)
                    Column {
                        Text(
                            text = "Premium (Free with Account)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("• Cloud storage & sync")
                        Text("• Unlimited insurance policies")
                        Text("• Advanced notifications")
                        Text("• Photo & PDF uploads")
                        Text("• Share with partner")
                        Text("• Cross-device sync")
                        Text("• Data backup & export")
                    }

                    if (!authState.canAccessPremiumFeatures) {
                        Button(
                            onClick = { authViewModel.exitGuestMode() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Upgrade, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upgrade to Premium")
                        }
                    }
                }
            }

            // Premium Features (only show if premium)
            authState.currentUser?.let { user ->
                if (user.subscriptionTier == SubscriptionTier.PREMIUM) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Partner Sharing",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (user.partnerId != null) {
                                Text(
                                    text = "✓ Sharing enabled with partner",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text("Share your insurance policies with your partner")

                                Button(
                                    onClick = { showPartnerDialog = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Add Partner")
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cloud Features",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text("• All data automatically synced to cloud")
                            Text("• Access from multiple devices")
                            Text("• Automatic backups")
                            Text("• Upload and store policy PDFs")
                        }
                    }
                }
            }

            // Sign out button
            authState.currentUser?.let {
                OutlinedButton(
                    onClick = { authViewModel.signOut() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out")
                }
            }

            authState.error?.let { errorMessage ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }

    // Partner Email Dialog
    if (showPartnerDialog) {
        AlertDialog(
            onDismissRequest = { showPartnerDialog = false },
            title = { Text("Add Partner") },
            text = {
                Column {
                    Text("Enter your partner's email address to share your insurance policies:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = partnerEmail,
                        onValueChange = { partnerEmail = it },
                        label = { Text("Partner's Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.shareWithPartner(partnerEmail)
                        showPartnerDialog = false
                        partnerEmail = ""
                    },
                    enabled = partnerEmail.isNotBlank()
                ) {
                    Text("Add Partner")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPartnerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}