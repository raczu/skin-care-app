package com.raczu.skincareapp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raczu.skincareapp.entities.Product

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
        modifier = modifier.clickable { isExpanded.value = !isExpanded.value },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = product.purpose ?: "",
                        style = MaterialTheme.typography.labelLarge,
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
                    style = MaterialTheme.typography.bodyLarge,
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