package com.raczu.skincareapp.ui.features.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Face
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.raczu.skincareapp.data.domain.models.product.Product
import com.raczu.skincareapp.data.domain.models.routine.Routine
import com.raczu.skincareapp.data.domain.models.routine.RoutineType
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.AppDatePickerDialog
import com.raczu.skincareapp.ui.components.BottomAddFloatingButton
import com.raczu.skincareapp.ui.components.TopBar
import com.raczu.skincareapp.ui.navigation.TopBarScreen
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(
    title: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RoutineViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val currentBackStackEntry = navController.currentBackStackEntry
    val shouldRefresh by currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refresh_routines", false)
        ?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(false) }

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadRoutinesForDate()
            currentBackStackEntry?.savedStateHandle?.set("refresh_routines", false)
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        AppDatePickerDialog(
            initialDate = uiState.selectedDate,
            onDateSelected = { newDate ->
                viewModel.onDateSelected(newDate)
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }

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
                canNavigateBack = false
            )
        },
        bottomBar = {
            BottomAddFloatingButton(
                onClick = { navController.navigate(TopBarScreen.AddRoutine.route) },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .fillMaxSize()
        ) {
            DateHeader(
                selectedDate = uiState.selectedDate,
                onPreviousDay = { viewModel.onDateSelected(uiState.selectedDate.minusDays(1)) },
                onNextDay = { viewModel.onDateSelected(uiState.selectedDate.plusDays(1)) },
                onCalendarClick = { showDatePicker = true }
            )

            DailyStatsCard(routines = uiState.routines)

            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.loadRoutinesForDate() },
                state = rememberPullToRefreshState(),
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.routines.isEmpty() && !uiState.isLoading) {
                    EmptyDayMessage()
                } else {
                    RoutineLazyList(
                        routines = uiState.routines,
                        onItemClick = {
                            navController.navigate("${TopBarScreen.RoutineDetails.route}/$it")
                        },
                        onDeleteClick = { routineId -> viewModel.deleteRoutine(routineId) },
                        onEditClick = {
                            navController.navigate("${TopBarScreen.EditRoutine.route}/$it")
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineLazyList(
    routines: List<Routine>,
    onItemClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = routines, key = { it.id }) { routine ->
            RoutineListItem(
                routine = routine,
                onClick = { onItemClick(routine.id) },
                onEditClick = { onEditClick(routine.id) },
                onDeleteClick = { onDeleteClick(routine.id) }
            )
        }
    }
}

@Composable
fun EmptyDayMessage(
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
            Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No routines logged for this day.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tap + to add one!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyDayMessagePreview() {
    EmptyDayMessage()
}

@Composable
fun LetterRoutineTypeBadge(type: RoutineType) {
    val (containerColor, contentColor) = when (type) {
        RoutineType.MORNING -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        RoutineType.NIGHT -> Color(0xFFE8EAF6) to Color(0xFF283593)
        RoutineType.DAILY -> Color(0xFFE0F2F1) to Color(0xFF00695C)
        RoutineType.OTHER -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
    }

    Box(
        modifier = Modifier
            .size(28.dp)
            .background(color = containerColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = type.name.first().toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LetterRoutineTypeBadgePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LetterRoutineTypeBadge(type = RoutineType.MORNING)
        LetterRoutineTypeBadge(type = RoutineType.NIGHT)
        LetterRoutineTypeBadge(type = RoutineType.DAILY)
        LetterRoutineTypeBadge(type = RoutineType.OTHER)
    }
}

@Composable
fun DailyStatsCard(routines: List<Routine>) {
    val completedCount = routines.size
    val types = routines.map { it.type }.distinct().sortedBy { it.ordinal }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Daily Summary",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$completedCount Routines Completed",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                types.forEach { type ->
                    LetterRoutineTypeBadge(type = type)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyStatsCardPreview() {
    val products = listOf(
        Product(id = "1", name = "Cleanser"),
        Product(id = "2", name = "Moisturizer"),
        Product(id = "3", name = "Sunscreen"),
    )
    DailyStatsCard(
        routines = listOf(
            Routine(
                id = "r1",
                type = RoutineType.MORNING,
                performedAt = LocalDateTime.now(),
                products = products
            ),
            Routine(
                id = "r2",
                type = RoutineType.NIGHT,
                performedAt = LocalDateTime.now(),
                products = products
            ),
            Routine(
                id = "r3",
                type = RoutineType.DAILY,
                performedAt = LocalDateTime.now(),
                products = products
            ),
            Routine(
                id = "r4",
                type = RoutineType.OTHER,
                performedAt = LocalDateTime.now(),
                products = products
            )
        )
    )
}

@Composable
fun RoutineListItem(
    routine: Routine,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val productsSummary = remember(routine.products) {
        if (routine.products.isEmpty()) {
            "No products recorded"
        } else {
            val firstTwo = routine.products.take(2).joinToString(", ") { it.name }
            val remainingCount = routine.products.size - 2

            if (remainingCount > 0) "$firstTwo + $remainingCount more"
            else firstTwo
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = routine.performedAt.format(timeFormatter),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    LetterRoutineTypeBadge(type = routine.type)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Face,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = productsSummary,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoutineItemCardPreview() {
    val products = listOf(
        Product(id = "1", name = "Cleanser"),
        Product(id = "2", name = "Toner"),
        Product(id = "3", name = "Serum"),
        Product(id = "4", name = "Moisturizer"),
        Product(id = "5", name = "Sunscreen"),
    )
    RoutineListItem(
        routine = Routine(
            id = "r1",
            type = RoutineType.MORNING,
            performedAt = LocalDateTime.now(),
            products = products,
            notes = "Felt great! My skin is glowing and hydrated after using these products."
        ),
        onEditClick = {},
        onDeleteClick = {},
        onClick = {}
    )
}

@Composable
fun DateHeader(
    selectedDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onCalendarClick: () -> Unit
) {
    val isToday = selectedDate == LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous Day",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { onCalendarClick() }
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isToday) "Today"
                else selectedDate.format(DateTimeFormatter.ofPattern("EEEE")),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = selectedDate.format(formatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        IconButton(onClick = onNextDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateHeaderPreview() {
    DateHeader(
        selectedDate = LocalDate.now(),
        onPreviousDay = {},
        onNextDay = {},
        onCalendarClick = {}
    )
}