package com.raczu.skincareapp.ui.features.routines

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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.TopBar
import com.raczu.skincareapp.data.local.entities.Product
import com.raczu.skincareapp.data.local.entities.Routine
import com.raczu.skincareapp.utils.toHumanReadableString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailsScreen(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoutineDetailsViewModel = viewModel(factory = AppViewModelProvider.factory)
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
        RoutineDetailsBody(
            routineDetailsUiState = viewModel.routineDetailsUiState,
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
            )
        )
    }
}

@Composable
fun RoutineDetailsBody(
    routineDetailsUiState: RoutineDetailsUiState,
    modifier: Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoutineDetailsInformationSection(
            routine = routineDetailsUiState.routine,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Products used in this routine",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        if (routineDetailsUiState.products.isEmpty()) {
            Text(
                text = "Seems like used products were removed ;(",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            RoutineProductList(
                products = routineDetailsUiState.products,
                contentPadding = PaddingValues(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun RoutineDetailsInformationSection(
    routine: Routine,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Routine information",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.DateRange, contentDescription = "Date icon")
            Text(
                text = routine.createdAt.toHumanReadableString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Face, contentDescription = "Type icon")
            Text(
                text = routine.type.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

}

@Composable
fun RoutineProductList(
    products: List<Product>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(items = products, key = { it.productId }) { product ->
            RoutineProductListItem(
                product = product,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RoutineProductListItem(
    product: Product,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { isExpanded = !isExpanded },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = product.purpose ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
            AnimatedVisibility(
                visible = isExpanded && !product.description.isNullOrBlank()
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