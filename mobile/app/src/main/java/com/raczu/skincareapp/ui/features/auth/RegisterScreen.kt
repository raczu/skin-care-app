package com.raczu.skincareapp.ui.features.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.di.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.uiState.isRegistrationSuccessful) {
        if (viewModel.uiState.isRegistrationSuccessful) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Account created! Welcome to SkinCare ✨",
                    duration = SnackbarDuration.Short
                )
            }
            kotlinx.coroutines.delay(2000)
            onRegisterSuccess()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        RegisterBody(
            uiState = viewModel.uiState,
            fields = viewModel.fields,
            onRegisterClick = { viewModel.register() },
            onLoginClick = onNavigateToLogin,
            modifier = Modifier.padding(padding).fillMaxWidth()
        )
    }
}


@Composable
fun RegisterHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Join us and start your skincare journey today! ✨",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun RegisterFooter(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Already have an account?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(
            onClick = onLoginClick,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.heightIn(min = 32.dp)
        ) {
            Text(
                text = "Log in",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun RegisterBody(
    uiState: RegisterUiState,
    fields: RegisterViewModel.RegisterFields,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        RegisterHeader()

        Spacer(modifier = Modifier.height(32.dp))

        RegisterForm(
            fields = fields,
            enabled = !uiState.isLoading,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.error != null) {
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRegisterClick,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Sign up")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        RegisterFooter(onLoginClick = onLoginClick)
    }
}

@Composable
fun RegisterForm(
    fields: RegisterViewModel.RegisterFields,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = fields.name.text,
                onValueChange = { fields.name.onValueChange(it) },
                label = { Text("Name") },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true,
                isError = fields.name.error != null,
                supportingText = {
                    if (fields.name.error != null) {
                        Text(fields.name.error!!)
                    }
                }
            )
            OutlinedTextField(
                value = fields.surname.text,
                onValueChange = { fields.surname.onValueChange(it) },
                label = { Text("Surname") },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true,
                isError = fields.surname.error != null,
                supportingText = {
                    if (fields.surname.error != null) {
                        Text(fields.surname.error!!)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fields.username.text,
            onValueChange = { fields.username.onValueChange(it) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = fields.username.error != null,
            supportingText = {
                if (fields.username.error != null) {
                    Text(fields.username.error!!)
                }
            }
        )
        OutlinedTextField(
            value = fields.email.text,
            onValueChange = { fields.email.onValueChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = fields.email.error != null,
            supportingText = {
                if (fields.email.error != null) {
                    Text(fields.email.error!!)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fields.password.text,
            onValueChange = { fields.password.onValueChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = fields.password.error != null,
            supportingText = {
                if (fields.password.error != null) {
                    Text(fields.password.error!!)
                }
            }
        )
        OutlinedTextField(
            value = fields.confirmPassword.text,
            onValueChange = { fields.confirmPassword.onValueChange(it) },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = fields.confirmPassword.error != null,
            supportingText = {
                if (fields.confirmPassword.error != null) {
                    Text(fields.confirmPassword.error!!)
                }
            }
        )
    }
}

@Preview()
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onRegisterSuccess = {},
        onNavigateToLogin = {}
    )
}