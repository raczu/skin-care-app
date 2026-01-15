package com.raczu.skincareapp.ui.features.routine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.raczu.skincareapp.data.domain.models.product.Product
import com.raczu.skincareapp.data.domain.models.routine.RoutineType
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.AppDatePickerDialog
import com.raczu.skincareapp.ui.components.AppTimePickerDialog
import com.raczu.skincareapp.ui.components.TopBar
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineFormScreen(
    title: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RoutineFormViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showProductSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(uiState.isOperationSuccessful) {
        if (uiState.isOperationSuccessful) {
            navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.set("refresh_routines", true)
            navController.popBackStack()
        }
    }

    if (showDatePicker) {
        AppDatePickerDialog(
            initialDate = viewModel.fields.date.value,
            onDateSelected = { newDate ->
                viewModel.fields.date.onValueChange(newDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        AppTimePickerDialog(
            initialTime = viewModel.fields.time.value,
            onTimeSelected = { newTime ->
                viewModel.fields.time.onValueChange(newTime)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showProductSheet) {
        ModalBottomSheet(onDismissRequest = { showProductSheet = false }) {
            ProductSelectionSheetContent(
                products = uiState.availableProducts,
                selectedIds = viewModel.fields.selectedProductIds.value,
                isLoading = uiState.isLoading,
                hasError = uiState.error != null && uiState.availableProducts.isEmpty(),
                onToggle = { viewModel.toggleProductSelection(it) }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                title = title,
                canNavigateBack = true,
                onBack = { navController.popBackStack() }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RoutineFormContent(
                fields = viewModel.fields,
                onDateClick = { showDatePicker = true },
                onTimeClick = { showTimePicker = true },
                onProductsClick = { showProductSheet = true },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveRoutine() },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (uiState.isEditMode) "Save Changes" else "Add Routine")
                }
            }
        }
    }
}

@Composable
fun ProductSelectionSheetContent(
    products: List<Product>,
    selectedIds: Set<String>,
    isLoading: Boolean,
    hasError: Boolean,
    onToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .padding(16.dp)
    ) {
        Text(
            "Select Products",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                hasError -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Failed to load products",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                products.isEmpty() -> {
                    Text(
                        text = "You haven't added any products to your shelf yet.",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(products) { product ->
                            val isSelected = selectedIds.contains(product.id)
                            ListItem(
                                headlineContent = { Text(product.name) },
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
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { onToggle(product.id) }
                                    )
                                },
                                modifier = Modifier.clickable { onToggle(product.id) }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineFormContent(
    fields: RoutineFormViewModel.RoutineFormFields,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onProductsClick: () -> Unit,
    enabled: Boolean
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoutineType.entries.forEach { type ->
                FilterChip(
                    selected = fields.type.value == type,
                    onClick = { if (enabled) fields.type.onValueChange(type) },
                    label = { Text(type.name) },
                    enabled = enabled
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = fields.date.value.format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = { Icon(Icons.Default.DateRange, null) },
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = enabled) { onDateClick() },
                enabled = false,
                colors = clickableOutlinedTextFieldColors()
            )
            OutlinedTextField(
                value = fields.time.value.format(timeFormatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Time") },
                trailingIcon = { Icon(Icons.Default.Info, null) },
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = enabled) { onTimeClick() },
                enabled = false,
                colors = clickableOutlinedTextFieldColors()
            )
        }

        val selectedCount = fields.selectedProductIds.value.size
        val productsText = if (selectedCount == 0) "" else "$selectedCount selected"

        OutlinedTextField(
            value = productsText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Products") },
            placeholder = { Text("Select products...") },
            trailingIcon = { Icon(Icons.Default.Add, null) },
            isError = fields.selectedProductIds.error != null,
            supportingText = fields.selectedProductIds.error?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { onProductsClick() },
            enabled = false,
            colors = clickableOutlinedTextFieldColors(isError = fields.selectedProductIds.error != null)
        )

        OutlinedTextField(
            value = fields.notes.value,
            onValueChange = { fields.notes.onValueChange(it) },
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("How did your skin feel?") },
            minLines = 3,
            maxLines = 5
        )
    }
}

@Composable
fun clickableOutlinedTextFieldColors(isError: Boolean = false) = OutlinedTextFieldDefaults.colors(
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledLabelColor = if (isError) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurfaceVariant,

    disabledBorderColor = if (isError) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.outline,

    disabledTrailingIconColor = if (isError) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurfaceVariant,
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledContainerColor = Color.Transparent,

    disabledSupportingTextColor = if (isError) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurfaceVariant,

    errorBorderColor = MaterialTheme.colorScheme.error,
    errorLabelColor = MaterialTheme.colorScheme.error,
    errorSupportingTextColor = MaterialTheme.colorScheme.error
)
