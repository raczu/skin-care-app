package com.raczu.skincareapp.ui.features.routines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.TopBar
import com.raczu.skincareapp.data.local.entities.Product
import com.raczu.skincareapp.utils.enums.RoutineType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineAddScreen(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoutineAddViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val products by viewModel.products.collectAsState()

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
        RoutineAddBody(
            routineAddUiState = viewModel.routineAddUiState,
            products = products,
            onValueChange = viewModel::updateUiState,
            onAdd = {
                viewModel.insert()
                onBack()
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .fillMaxWidth(),
            )
    }
}

@Composable
fun RoutineAddBody(
    routineAddUiState: RoutineAddUiState,
    products: List<Product>,
    onValueChange: (RoutineFormData) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoutineForm(
            formData = routineAddUiState.formData,
            products = products,
            onValueChange = onValueChange
        )
        Button(
            onClick = onAdd,
            enabled = routineAddUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Note new routine")
        }
    }
}

@Composable
fun RoutineForm(
    formData: RoutineFormData,
    products: List<Product>,
    onValueChange: (RoutineFormData) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Text(
            text = "Select routine type",
            style = MaterialTheme.typography.labelLarge,
        )
        RoutineTypeSelector(
            selectedType = formData.type,
            onSelect = { onValueChange(formData.copy(type = it)) },
        )
        Text(
            text = "Select used products",
            style = MaterialTheme.typography.labelLarge,
        )
        if (products.isEmpty()) {
            Text(
                text = "Add products to be able to select them here!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        } else {
            RoutineProductsSelector(
                products = products,
                selectedProducts = formData.products,
                onClick = {
                    val updatedProducts = if (formData.products.contains(it)) {
                        formData.products - it
                    } else {
                        formData.products + it
                    }
                    onValueChange(formData.copy(products = updatedProducts))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineTypeSelector(
    selectedType: RoutineType,
    onSelect: (RoutineType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Card(
                modifier = Modifier.padding(8.dp),
                onClick = { onSelect(RoutineType.MORNING) },
                colors = if (selectedType == RoutineType.MORNING) CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) else CardDefaults.cardColors(),
            ) {
                Text(
                    text = RoutineType.MORNING.name,
                    modifier = Modifier
                        .padding(8.dp)
                        .widthIn(min = 90.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Card(
                modifier = Modifier.padding(8.dp),
                onClick = { onSelect(RoutineType.NIGHT) },
                colors = if (selectedType == RoutineType.NIGHT) CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) else CardDefaults.cardColors(),
            ) {
                Text(
                    text = RoutineType.NIGHT.name,
                    modifier = Modifier
                        .padding(8.dp)
                        .widthIn(min = 90.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun RoutineProductsSelector(
    products: List<Product>,
    selectedProducts: List<Product>,
    onClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(items = products, key = { it.productId }) { product ->
            RoutineProductsSelectorEntry(
                product = product,
                onClick = onClick,
                isSelected = selectedProducts.contains(product),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoutineProductsSelector() {
    RoutineProductsSelector(
        products = listOf(
            Product(
                productId = 0,
                name = "Cleanser",
                purpose = "Cleanse skin",
            ),
            Product(
                productId = 1,
                name = "Moisturizer",
                purpose = "Hydrate skin",
            ),
            Product(
                productId = 2,
                name = "Sunscreen",
                purpose = "Protect skin",
            ),
        ),
        selectedProducts = listOf(),
        onClick = {},
        contentPadding = PaddingValues(16.dp)
    )
}

@Composable
fun RoutineProductsSelectorEntry(
    product: Product,
    onClick: (Product) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clickable { onClick(product) },
        colors = if (isSelected) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) else CardDefaults.cardColors(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = product.purpose ?: "",
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoutineProductsSelectorEntry() {
    RoutineProductsSelectorEntry(
        product = Product(
            name = "Cleanser",
            purpose = "Cleanse skin",
        ),
        onClick = {},
        isSelected = false,
        modifier = Modifier.padding(16.dp)
    )
}