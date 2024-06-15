package com.raczu.skincareapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raczu.skincareapp.components.BottomAddFloatingButton
import com.raczu.skincareapp.components.ProductForm
import com.raczu.skincareapp.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    title: String,
    navigateToProductAdd: () -> Unit,
    navigateToProductEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopBar(
                title = title,
                canNavigateBack = false
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
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text("Products screen")
        }
    }
}