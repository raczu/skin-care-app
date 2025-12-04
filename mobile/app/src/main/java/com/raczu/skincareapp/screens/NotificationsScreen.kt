package com.raczu.skincareapp.screens

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.AppViewModelProvider
import com.raczu.skincareapp.components.TimePickerDialog
import com.raczu.skincareapp.components.TopBar
import com.raczu.skincareapp.enums.RoutineType
import com.raczu.skincareapp.views.RoutineNotificationUiState
import com.raczu.skincareapp.views.RoutineNotificationViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    title: String,
    modifier: Modifier = Modifier,
    viewModel: RoutineNotificationViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    Scaffold(
        topBar = {
            TopBar(
                title = title,
                canNavigateBack = false,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        NotificationScreenBody(
            routineNotificationUiState = viewModel.routineNotificationUiState,
            onAdd = viewModel::insert,
            onUiUpdate = viewModel::updateUiState,
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            )
        )
    }
}

@Composable
fun NotificationScreenBody(
    routineNotificationUiState: RoutineNotificationUiState,
    onAdd: (RoutineType) -> Unit,
    onUiUpdate: (RoutineType, LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMorningRoutineTimeInput by remember { mutableStateOf(false) }
    var showNightRoutineTimeInput by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Morning routine reminder",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Card(
            modifier = Modifier
                .clickable { showMorningRoutineTimeInput = true }
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notification icon")
                Text(
                    text = routineNotificationUiState.morningRoutineTime?.toString() ?: "Not set",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (showMorningRoutineTimeInput) {
            NotificationTimeInput(
                time = routineNotificationUiState.morningRoutineTime,
                onTimeSelected = {
                    onUiUpdate(RoutineType.MORNING, it)
                },
                onDismiss = { showMorningRoutineTimeInput = false },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = "Night routine reminder",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Card(
            modifier = Modifier
                .clickable { showNightRoutineTimeInput = true }
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notification icon")
                Text(
                    text = routineNotificationUiState.nightRoutineTime?.toString() ?: "Not set",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (showNightRoutineTimeInput) {
            NotificationTimeInput(
                time = routineNotificationUiState.nightRoutineTime,
                onTimeSelected = {
                    onUiUpdate(RoutineType.NIGHT, it)
                },
                onDismiss = { showNightRoutineTimeInput = false },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = "*Click on the desired routine to set a reminder time",
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onAdd(RoutineType.MORNING)
                onAdd(RoutineType.NIGHT)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Set reminders")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTimeInput(
    time: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timePickerState = rememberTimePickerState(
        initialHour = time?.hour ?: LocalTime.now().hour,
        initialMinute = time?.minute ?: LocalTime.now().minute
    )

    TimePickerDialog(
        onCancel = onDismiss,
        onConfirm = {
            onTimeSelected(
                LocalTime.of(timePickerState.hour, timePickerState.minute)
            )
            onDismiss()
        }
    ) {
        TimeInput(
            modifier = modifier,
            state = timePickerState
        )
    }
}