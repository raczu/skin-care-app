package com.raczu.skincareapp.ui.features.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.BottomAddFloatingButton
import com.raczu.skincareapp.ui.components.TopBar
import com.raczu.skincareapp.data.local.entities.Product

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

@Composable
fun ProductList(
    products: List<Product>,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(items = products, key = { it.productId }) { product ->
            ProductListItem(
                product = product,
                onEdit = { onEdit(product) },
                onDelete = { onDelete(product) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductList() {
    val products = listOf(
        Product(1, "Face cream", purpose = "Moisturizing"),
        Product(2, "Face mask", purpose = "Purifying"),
        Product(3, "Face serum", purpose = "Brightening"),
    )
    ProductList(products = products, onEdit = {}, onDelete = {}, contentPadding = PaddingValues(16.dp))
}

@Composable
fun ProductListItem(
    product: Product,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    modifier: Modifier
) {
    val isExpanded = remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { isExpanded.value = !isExpanded.value },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = product.purpose ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Row {
                    IconButton(onClick = { onEdit(product) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onDelete(product) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            AnimatedVisibility(
                visible = isExpanded.value && !product.description.isNullOrBlank()
            ) {
                Text(
                    text = product.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .animateContentSize()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductListItem() {
    val product = Product(
        1,
        "Face cream",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        "Moisturizing"
    )
    ProductListItem(
        product = product,
        onEdit = {},
        onDelete = {},
        modifier = Modifier.fillMaxWidth()
    )
}