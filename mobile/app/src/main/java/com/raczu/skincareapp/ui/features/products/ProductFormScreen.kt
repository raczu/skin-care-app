package com.raczu.skincareapp.ui.features.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    title: String,
    onNavigateBack: () -> Unit,
    viewModel: ProductFormViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isOperationSuccessful) {
        if (uiState.isOperationSuccessful) onNavigateBack()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(
                it,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                title = title,
                canNavigateBack = true,
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductFormContent(
                fields = viewModel.fields,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveProduct() },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (uiState.isEditMode) "Save Changes" else "Add Product"
                    )
                }
            }
        }
    }
}

@Composable
fun ProductFormContent(
    fields: ProductFormViewModel.ProductFormFields,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        OutlinedTextField(
            value = fields.name.value,
            onValueChange = { fields.name.onValueChange(it) },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth(),
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
            value = fields.brand.value,
            onValueChange = { fields.brand.onValueChange(it) },
            label = { Text("Brand") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )
        OutlinedTextField(
            value = fields.purpose.value,
            onValueChange = { fields.purpose.onValueChange(it) },
            label = { Text("Purpose") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )
        OutlinedTextField(
            value = fields.description.value,
            onValueChange = { fields.description.onValueChange(it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            minLines = 3
        )
    }
}