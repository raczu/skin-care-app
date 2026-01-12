package com.raczu.skincareapp.ui.features.products.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class ProductFormData(
    val name: String = "",
    val description: String? = null,
    val purpose: String? = null,
)

@Composable
fun ProductForm(
    formData: ProductFormData,
    modifier: Modifier = Modifier,
    onValueChange: (ProductFormData) -> Unit = {},
    enabled: Boolean = false
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ){
        OutlinedTextField(
            value = formData.name,
            onValueChange = { onValueChange(formData.copy(name = it)) },
            label = { Text("Product name") },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = formData.description ?: "",
            onValueChange = { onValueChange(formData.copy(description = it)) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formData.purpose ?: "",
            onValueChange = { onValueChange(formData.copy(purpose = it)) },
            label = { Text("Purpose") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = if (enabled) {
                { Text("e.g. Moisturizing, Purifying, Brightening") } } else { null }
        )
    }
}
