package com.raczu.skincareapp.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.AppViewModelProvider
import com.raczu.skincareapp.components.BottomAddFloatingButton
import com.raczu.skincareapp.components.TopBar
import com.raczu.skincareapp.entities.Routine
import com.raczu.skincareapp.enums.RoutineType
import com.raczu.skincareapp.extensions.toHumanReadableString
import com.raczu.skincareapp.extensions.toInstant
import com.raczu.skincareapp.views.RoutineUiState
import com.raczu.skincareapp.views.RoutineViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(
    title: String,
    navigateToRoutineAdd: () -> Unit,
    navigateToRoutineDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoutineViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val routineUiState by viewModel.routineUiState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = title,
                canNavigateBack = false
            )
        },
        bottomBar = {
            BottomAddFloatingButton(
                onClick = navigateToRoutineAdd,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        RoutineBody(
            routineUiState = routineUiState,
            onUpdate = viewModel::updateUiState,
            onClick = navigateToRoutineDetails,
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
fun RoutineBody(
    routineUiState: RoutineUiState,
    onUpdate: (Long) -> Unit,
    onClick: (Int) -> Unit,
    onDelete: (Routine) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DateSection(
            timestamp = routineUiState.timestamp,
            onUpdate = onUpdate,
            modifier = Modifier.padding(16.dp),
            highlightMorning = routineUiState.routines.any { it.type == RoutineType.MORNING },
            highlightNight = routineUiState.routines.any { it.type == RoutineType.NIGHT }
        )
        Text(
            text = "Noted routines at that day",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        if (routineUiState.routines.isEmpty()) {
            Text(
                text = "Any routines noted at that day ;(",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            RoutineList(
                routines = routineUiState.routines,
                onClick = { onClick(it.routineId) },
                onDelete = { onDelete(it) },
                contentPadding = PaddingValues(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun DateSection(
    timestamp: Long,
    onUpdate: (Long) -> Unit,
    modifier: Modifier = Modifier,
    highlightMorning: Boolean = false,
    highlightNight: Boolean = false
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        RoutineDateDialogPicker(
            timestamp = timestamp,
            onDateSelected = onUpdate,
            onDismiss = { showDatePicker = false },
            modifier = Modifier
        )
    }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(onClick = { showDatePicker = true }
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Calendar")
            }
            Text(
                text = timestamp.toInstant().toHumanReadableString("dd MMM yyyy"),
                style = MaterialTheme.typography.titleMedium,
            )
            RoutineTypeInformer(
                highlightMorning = highlightMorning,
                highlightNight = highlightNight
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDateDialogPicker(
    timestamp: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = timestamp.toInstant().toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onDateSelected(
                        datePickerState.selectedDateMillis ?: timestamp
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            modifier = modifier,
            state = datePickerState
        )
    }
}

@Composable
fun RoutineTypeInformer(
    modifier: Modifier = Modifier,
    highlightMorning: Boolean = false,
    highlightNight: Boolean = false
) {
    val highlightedCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

    val normalCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.outline,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

    Row(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
            colors = if (highlightMorning) highlightedCardColors
            else normalCardColors,
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
            colors = if (highlightNight) highlightedCardColors
            else normalCardColors,
        ) {
            Text(
                text = RoutineType.NIGHT.name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .widthIn(min = 90.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDateSection() {
    DateSection(
        timestamp = Date().time,
        highlightMorning = true,
        highlightNight = false,
        onUpdate = {}
    )
}

@Composable
fun RoutineList(
    routines: List<Routine>,
    onClick: (Routine) -> Unit,
    onDelete: (Routine) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(items = routines, key = { it.routineId }) { routine ->
            RoutineListItem(
                routine = routine,
                onClick = { onClick(routine) },
                onDelete = { onDelete(routine) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RoutineListItem(
    routine: Routine,
    onClick: (Routine) -> Unit,
    onDelete: (Routine) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick(routine) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = routine.createdAt.toHumanReadableString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = routine.type.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = { onDelete(routine) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
