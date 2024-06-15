package com.raczu.skincareapp.screens

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.AppViewModelProvider
import com.raczu.skincareapp.components.ProductForm
import com.raczu.skincareapp.components.ProductFormData
import com.raczu.skincareapp.components.TopBar
import com.raczu.skincareapp.views.ProductEditUiState
import com.raczu.skincareapp.views.ProductEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductEditViewModel = viewModel(factory = AppViewModelProvider.factory)
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
        ProductEditBody(
            productEditUiState = viewModel.productEditUiState,
            onValueChange = viewModel::updateUiState,
            onUpdate = {
                viewModel.update()
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
fun ProductEditBody(
    productEditUiState: ProductEditUiState,
    onValueChange: (ProductFormData) -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProductForm(
            formData = productEditUiState.formData,
            onValueChange = onValueChange
        )
        Button(
            onClick = onUpdate,
            enabled = productEditUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Update product")
        }
    }
}