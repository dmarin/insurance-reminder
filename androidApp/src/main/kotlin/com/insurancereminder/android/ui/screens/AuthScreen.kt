package com.insurancereminder.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.insurancereminder.shared.presentation.AuthState
import com.insurancereminder.shared.presentation.LoginFormState
import com.insurancereminder.android.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val loginFormState by viewModel.loginFormState.collectAsState()

    LaunchedEffect(authState) {
        if (authState.isAuthenticated || authState.isGuestMode) {
            onAuthSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Insurance Reminder",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Manage your insurance policies with ease",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = loginFormState.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = !loginFormState.isEmailValid,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = loginFormState.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Password") },
            visualTransformation = if (loginFormState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = !loginFormState.isPasswordValid,
            trailingIcon = {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(
                        imageVector = if (loginFormState.showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (loginFormState.showPassword) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (loginFormState.isSignUpMode) {
            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = loginFormState.confirmPassword,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = !loginFormState.passwordsMatch,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Button
        Button(
            onClick = {
                if (loginFormState.isSignUpMode) {
                    viewModel.signUp()
                } else {
                    viewModel.signIn()
                }
            },
            enabled = !authState.isLoading && if (loginFormState.isSignUpMode) loginFormState.isValidForSignUp else loginFormState.isValidForSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (loginFormState.isSignUpMode) "Sign Up" else "Sign In")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Switch mode button
        TextButton(
            onClick = {
                if (loginFormState.isSignUpMode) {
                    viewModel.switchToSignIn()
                } else {
                    viewModel.switchToSignUp()
                }
            }
        ) {
            Text(
                if (loginFormState.isSignUpMode)
                    "Already have an account? Sign In"
                else
                    "Don't have an account? Sign Up"
            )
        }

        // Forgot password button (only in sign-in mode)
        if (!loginFormState.isSignUpMode) {
            TextButton(
                onClick = { viewModel.sendPasswordResetEmail() },
                enabled = loginFormState.email.isNotEmpty() && loginFormState.isEmailValid
            ) {
                Text("Forgot Password?")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Guest mode option
        OutlinedButton(
            onClick = { viewModel.useAsGuest() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue as Guest (Local Storage)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Guest mode: Store data locally (max 5 policies)\nSign up for unlimited cloud storage and sharing",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Success message
        authState.successMessage?.let { successMessage ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = successMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Error message
        authState.error?.let { errorMessage ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}