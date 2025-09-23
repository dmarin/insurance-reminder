package com.insurancereminder.android.ui.navigation

import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.insurancereminder.android.ui.screens.AddInsuranceScreen
import com.insurancereminder.android.ui.screens.AuthScreen
import com.insurancereminder.android.ui.screens.InsuranceListScreen
import com.insurancereminder.android.ui.screens.ProfileScreen
import com.insurancereminder.shared.presentation.AuthState
import com.insurancereminder.android.viewmodel.AuthViewModel
import com.insurancereminder.android.viewmodel.InsuranceViewModel

@Composable
fun InsuranceReminderApp() {
    val navController = rememberNavController()
    val insuranceViewModel: InsuranceViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current
    @OptIn(androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi::class)
    val windowSizeClass = calculateWindowSizeClass(context as androidx.activity.ComponentActivity)

    val authState by authViewModel.authState.collectAsState()

    // Navigate to auth screen when user exits guest mode
    LaunchedEffect(authState.isAuthenticated, authState.isGuestMode) {
        if (!authState.isAuthenticated && !authState.isGuestMode) {
            navController.navigate("auth") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = when {
            authState.isLoading -> "auth"
            authState.isAuthenticated || authState.isGuestMode -> "insurance_list"
            else -> "auth"
        }
    ) {
        composable("auth") {
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate("insurance_list") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("insurance_list") {
            InsuranceListScreen(
                viewModel = insuranceViewModel,
                authViewModel = authViewModel,
                windowSizeClass = windowSizeClass,
                onAddInsurance = {
                    navController.navigate("add_insurance")
                },
                onEditInsurance = { insurance ->
                    // Store the insurance in ViewModel for editing
                    insuranceViewModel.setInsuranceToEdit(insurance)
                    navController.navigate("add_insurance")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }
        composable("add_insurance") {
            AddInsuranceScreen(
                viewModel = insuranceViewModel,
                onNavigateBack = {
                    insuranceViewModel.setInsuranceToEdit(null) // Clear edit state
                    navController.popBackStack()
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                authViewModel = authViewModel
            )
        }
    }
}