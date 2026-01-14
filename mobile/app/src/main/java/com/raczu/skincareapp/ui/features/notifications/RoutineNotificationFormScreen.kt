package com.raczu.skincareapp.ui.features.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.data.domain.models.notification.NotificationFrequency
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.AppTimePickerDialog
import com.raczu.skincareapp.ui.components.TopBar
import com.raczu.skincareapp.ui.features.routine.clickableOutlinedTextFieldColors
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineNotificationFormScreen(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoutineNotificationFormViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showTimePicker by remember { mutableStateOf(false) }
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
            onNavigateBack()
        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RoutineNotificationFormContent(
                fields = viewModel.fields,
                enabled = !uiState.isLoading,
                onTimeClick = { showTimePicker = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveNotificationRule() },
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
                    Text(if (uiState.isEditMode) "Save Changes" else "Add Reminder")
                }
            }
        }
    }
}

@Composable
fun RoutineNotificationFormContent(
    fields: RoutineNotificationFormViewModel.RoutineNotificationFormFields,
    enabled: Boolean,
    onTimeClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

        OutlinedTextField(
            value = fields.time.value.format(timeFormatter),
            onValueChange = {},
            readOnly = true,
            label = { Text("Time") },
            trailingIcon = { Icon(Icons.Default.Info, null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { onTimeClick() },
            enabled = false,
            colors = clickableOutlinedTextFieldColors()
        )

        FrequencyDropdown(
            selectedFrequency = fields.frequency.value,
            onFrequencySelected = { fields.frequency.onValueChange(it) },
            enabled = enabled
        )

        AnimatedContentWrapper(fields.frequency.value) { frequency ->
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                when (frequency) {
                    NotificationFrequency.EVERY_N_DAYS -> {
                        OutlinedTextField(
                            value = fields.everyN.value.toString(),
                            onValueChange = {
                                if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                    fields.everyN.onValueChange(it.toInt())
                                } else if (it.isEmpty()) {
                                    fields.everyN.onValueChange(1)
                                }
                            },
                            label = { Text("Days interval") },
                            suffix = { Text("days") },
                            singleLine = true,
                            isError = fields.everyN.error != null,
                            supportingText = fields.everyN.error?.let { { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    NotificationFrequency.CUSTOM -> {
                        Text(
                            text = "Select days of week",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                        WeekdaySelector(
                            selectedDaysMask = fields.weekdays.value,
                            onDayToggle = { index ->
                                val newList = fields.weekdays.value.toMutableList()
                                newList[index] = if (newList[index] == 1) 0 else 1
                                fields.weekdays.onValueChange(newList)
                            },
                            enabled = enabled
                        )
                        if (fields.weekdays.error != null) {
                            Text(
                                text = fields.weekdays.error ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                    else -> { }
                }
            }
        }
    }
}

@Composable
fun WeekdaySelector(
    selectedDaysMask: List<Int>,
    onDayToggle: (Int) -> Unit,
    enabled: Boolean,
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        days.forEachIndexed { index, label ->
            FilterChip(
                selected = selectedDaysMask.getOrElse(index) { 0 } == 1,
                onClick = { onDayToggle(index) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                enabled = enabled
            )
        }
    }
}

@Composable
fun AnimatedContentWrapper(targetState: Any, content: @Composable (Any) -> Unit) {
    // TODO: Add nice animation.
    content(targetState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrequencyDropdown(
    selectedFrequency: NotificationFrequency,
    onFrequencySelected: (NotificationFrequency) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedFrequency.name.replace("_", " "),
            onValueChange = {},
            readOnly = true,
            label = { Text("Frequency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            NotificationFrequency.entries.forEach { frequency ->
                DropdownMenuItem(
                    text = { Text(frequency.name.replace("_", " ")) },
                    onClick = {
                        onFrequencySelected(frequency)
                        expanded = false
                    }
                )
            }
        }
    }
}
