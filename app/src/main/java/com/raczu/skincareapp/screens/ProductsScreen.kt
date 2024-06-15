package com.raczu.skincareapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import com.raczu.skincareapp.AppViewModelProvider
import com.raczu.skincareapp.components.BottomAddFloatingButton
import com.raczu.skincareapp.components.ProductList
import com.raczu.skincareapp.components.TopBar
import com.raczu.skincareapp.entities.Product
import com.raczu.skincareapp.views.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    title: String,
    navigateToProductAdd: () -> Unit,
    navigateToProductEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductsViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val productsUiState by viewModel.productsUiState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = title,
                canNavigateBack = false,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        },
        bottomBar = {
            BottomAddFloatingButton(
                onClick = navigateToProductAdd,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ProductsBody(
            products = productsUiState.products,
            onEdit = navigateToProductEdit,
            onDelete = viewModel::delete,
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            )
        )
    }
}

@Composable
fun ProductsBody(
    products: List<Product>,
    onEdit: (Int) -> Unit,
    onDelete: (Product) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (products.isEmpty()) {
            Text(
                text = "Add products to see them here!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxWidth()
            )
        } else {
            ProductList(
                products = products,
                onEdit = { onEdit(it.productId) },
                onDelete = { onDelete(it) },
                contentPadding = contentPadding
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductsBody() {
    ProductsBody(
        products = listOf(),
        onEdit = {},
        onDelete = {}
    )
}