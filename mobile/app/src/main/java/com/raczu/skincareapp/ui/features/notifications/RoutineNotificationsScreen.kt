package com.raczu.skincareapp.ui.features.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raczu.skincareapp.data.domain.models.notification.NotificationFrequency
import com.raczu.skincareapp.data.domain.models.notification.NotificationRule
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.BottomAddFloatingButton
import com.raczu.skincareapp.ui.components.TopBar
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineNotificationsScreen(
    title: String,
    onNavigateToNotificationAdd: () -> Unit,
    onNavigateToNotificationEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoutineNotificationsViewModel = viewModel(factory = AppViewModelProvider.factory)
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

    val pullToRefreshState = rememberPullToRefreshState()

    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= 33) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) viewModel.onNotificationPermissionGranted()
        }
    )

    val askForPermission = {
        if (Build.VERSION.SDK_INT >= 33) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                title = title,
                canNavigateBack = false
            )
        },
        bottomBar = {
            BottomAddFloatingButton(
                onClick = {
                    if (hasNotificationPermission) onNavigateToNotificationAdd()
                    else askForPermission()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        },
        modifier = modifier
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadNotifications() },
            state = pullToRefreshState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (uiState.notifications.isEmpty() && !uiState.isLoading) {
                EmptyNotificationState(
                    hasPermission = hasNotificationPermission,
                    onEnableClicked = askForPermission
                )
            } else {
                RoutineNotificationsLazyList(
                    notifications = uiState.notifications,
                    onItemClick = { ruleId -> onNavigateToNotificationEdit(ruleId) },
                    onItemDeleteClick = { ruleId -> viewModel.deleteNotification(ruleId) },
                    onItemToggleClick = viewModel::toggleNotification,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun RoutineNotificationsLazyList(
    notifications: List<NotificationRule>,
    onItemClick: (String) -> Unit,
    onItemDeleteClick: (String) -> Unit,
    onItemToggleClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items = notifications, key = { it.id }) { rule ->
            RoutineNotificationRuleItem(
                rule = rule,
                onToggleClick = { isEnabled -> onItemToggleClick(rule.id, isEnabled) },
                onClick = { onItemClick(rule.id) },
                onDeleteClick = { onItemDeleteClick(rule.id) }
            )
        }
    }
}


@Composable
fun RoutineFrequencyBadge(frequency: NotificationFrequency) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = frequency.name.replace("_", " "),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoutineFrequencyBadgePreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoutineFrequencyBadge(frequency = NotificationFrequency.DAILY)
        RoutineFrequencyBadge(frequency = NotificationFrequency.WEEKDAY_ONLY)
        RoutineFrequencyBadge(frequency = NotificationFrequency.EVERY_N_DAYS)
    }
}

@Composable
fun RoutineNotificationRuleItem(
    rule: NotificationRule,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleClick: (Boolean) -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = rule.timeOfDay.format(timeFormatter),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    RoutineFrequencyBadge(rule.frequency)
                }

                Spacer(modifier = Modifier.height(4.dp))

                val frequencyDescription = when (rule) {
                    is NotificationRule.Once -> "Only once"
                    is NotificationRule.Daily -> "Every day"
                    is NotificationRule.WeekdayOnly -> "Monday to Friday"
                    is NotificationRule.EveryNDays -> "Every ${rule.everyN} days"
                    is NotificationRule.Custom -> {
                        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        val activeDays = rule.weekdays.mapIndexedNotNull { index, value ->
                            if (value == 1 && index < dayNames.size) dayNames[index] else null
                        }
                        if (activeDays.isEmpty()) "No days selected"
                        else activeDays.joinToString(", ")
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = frequencyDescription,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Tap to edit details",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )

            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                    )
                }

                Switch(
                    checked = rule.enabled,
                    onCheckedChange = onToggleClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationRuleItemPreview() {
    RoutineNotificationRuleItem(
        rule = NotificationRule.Daily(
            id = "1",
            timeOfDay = java.time.LocalTime.of(8, 0),
            frequency = NotificationFrequency.DAILY,
            enabled = true
        ),
        onClick = {},
        onToggleClick = {},
        onDeleteClick = {}
    )
}

@Composable
fun EmptyNotificationState(
    hasPermission: Boolean,
    onEnableClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (hasPermission) Icons.Default.Info else Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasPermission) "No reminders set yet." else "Notifications disabled.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        val subtext = if (hasPermission) {
            "Set up notification rules to stay consistent with your skincare routine and see better results!"
        } else {
            "To stay consistent with your routine, please enable notifications first."
        }

        Text(
            text = subtext,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )

        if (!hasPermission) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onEnableClicked,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Enable Notifications")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyNotificationRuleListPreview() {
    EmptyNotificationState(hasPermission = false, onEnableClicked = { })
}

