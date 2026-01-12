package com.raczu.skincareapp.ui.features.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.features.products.components.ProductForm
import com.raczu.skincareapp.ui.features.products.components.ProductFormData
import com.raczu.skincareapp.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAddScreen(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductAddViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    Scaffold(
        topBar = {
            TopBar(
                title = title,
                canNavigateBack = true,
                onBack = onBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ProductAddBody(
            productAddUiState = viewModel.productAddUiState,
            onValueChange = viewModel::updateUiState,
            onAdd = {
                viewModel.insert()
                onBack()
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .fillMaxWidth()
        )
    }
}

@Composable
fun ProductAddBody(
    productAddUiState: ProductAddUiState,
    onValueChange: (ProductFormData) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProductForm(
            formData = productAddUiState.formData,
            onValueChange = onValueChange,
            enabled = true
        )
        Button(
            onClick = onAdd,
            enabled = productAddUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Add Product")
        }
    }
}

@Preview
@Composable
fun ProductAddScreenPreview() {
    ProductAddScreen(
        title = "Add Product",
        onBack = {},
    )
}