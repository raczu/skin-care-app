package com.raczu.skincareapp.ui.features.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.data.domain.models.product.Product
import com.raczu.skincareapp.data.domain.models.routine.Routine
import com.raczu.skincareapp.data.domain.models.routine.RoutineType
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.CenteredLoading
import com.raczu.skincareapp.ui.components.TopBar
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailsScreen(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoutineDetailsViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
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
        },
        modifier = modifier
    ) { padding ->
        if (uiState.isLoading && uiState.routine == null) {
            CenteredLoading(modifier = Modifier.padding(padding))
        } else {
            RoutineContent(
                routine = uiState.routine!!,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun RoutineContent(
    routine: Routine,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RoutineTypeChip(type = routine.type)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = routine.performedAt.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = routine.performedAt.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))

        RoutineProductsList(
            products = routine.products,
            modifier = Modifier.fillMaxWidth()
        )

        if (!routine.notes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = routine.notes,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RoutineTypeChip(type: RoutineType) {
    val (bgColor, contentColor) = when (type) {
        RoutineType.MORNING -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        RoutineType.NIGHT -> Color(0xFFE8EAF6) to Color(0xFF283593)
        RoutineType.DAILY -> Color(0xFFE0F2F1) to Color(0xFF00695C)
        RoutineType.OTHER -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
    }

    Surface(
        color = bgColor,
        contentColor = contentColor,
        shape = CircleShape
    ) {
        Text(
            text = type.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun RoutineProductsList(
    products: List<Product>,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) {
        Text(
            text = "No products recorded.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = modifier
        )
    } else {
        Column(modifier = modifier.fillMaxWidth()) {
            products.forEachIndexed { index, product ->
                ListItem(
                    headlineContent = {
                        Text(product.name, fontWeight = FontWeight.Medium)
                    },
                    supportingContent = product.brand?.let {
                        {
                            Text(
                                text = it.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                if (index < products.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoutineContentPreview() {
    val sampleProducts = listOf(
        Product(id = "1", name = "Hydrating Serum", brand = "GlowCo"),
        Product(id = "2", name = "Vitamin C Cream", brand = "BrightSkin"),
        Product(id = "3", name = "Sunscreen SPF 50", brand = "SunSafe")
    )

    val sampleRoutine = Routine(
        id = "routine1",
        type = RoutineType.MORNING,
        performedAt = java.time.LocalDateTime.now(),
        products = sampleProducts,
        notes = "Felt great! My skin is glowing."
    )

    RoutineContent(routine = sampleRoutine)
}