package si.hozana.lekcionar.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import si.hozana.lekcionar.ActivityListener
import si.hozana.lekcionar.ui.routes.Screen
import si.hozana.lekcionar.ui.theme.AppTheme
import si.hozana.lekcionar.viewModel.LekcionarViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendar(viewModel: LekcionarViewModel, navController: NavController, actListener: ActivityListener) {

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Koledar", style = AppTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.HOME.name)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = AppTheme.colorScheme.primary,
                    titleContentColor = AppTheme.colorScheme.headerContent,
                    navigationIconContentColor = AppTheme.colorScheme.headerContent
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxHeight()
                    .padding(innerPadding)
            ) {
                CalendarSection(viewModel = viewModel, navController = navController, activityListener = actListener)
            }
        }
    )
}

@Composable
private fun CalendarSection(viewModel: LekcionarViewModel, navController: NavController, activityListener: ActivityListener) {
    val currentDate by viewModel.selectedDate.collectAsState()

    val firstDataTimestamp by viewModel.firstDataTimestamp.collectAsState()
    val lastDataTimestamp by viewModel.lastDataTimestamp.collectAsState()

    Log.d("Calendar", "FirstDataTimestamp: $firstDataTimestamp")
    Log.d("Calendar", "LastDataTimestamp: $lastDataTimestamp")

    val currentMonth = remember { YearMonth.parse(currentDate, viewModel.dateFormatter) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }

    var calendarSelectedDate by remember { mutableStateOf(currentDate) }

    if (firstDataTimestamp != 0L && lastDataTimestamp != 0L) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
        ) {
            val state = rememberCalendarState(
                startMonth = YearMonth.from(timestampToLocalDate(firstDataTimestamp)),
                endMonth = YearMonth.from(timestampToLocalDate(lastDataTimestamp)),
                firstVisibleMonth = currentMonth,
                firstDayOfWeek = daysOfWeek.first()
            )
            val coroutineScope = rememberCoroutineScope()
            val visibleMonth = rememberFirstMostVisibleMonth(state, viewportPercent = 90f)

            SimpleCalendarTitle(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                currentMonth = visibleMonth.yearMonth,
                goToPrevious = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                    }
                },
                goToNext = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                    }
                }
            )

            HorizontalCalendar(
                modifier = Modifier.padding(top = 30.dp),
                state = state,
                dayContent = { day ->
                    Day(day,
                        viewModel = viewModel,
                        calendarSelectedDate = calendarSelectedDate
                    ) { newSelectedDate ->
                        calendarSelectedDate = newSelectedDate
                    }
                },
                monthHeader = {
                    MonthHeader(daysOfWeek = daysOfWeek)
                },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (calendarSelectedDate != currentDate) {
                            activityListener.stopClick()
                        }
                        navController.navigate(Screen.HOME.name)
                        viewModel.updateSelectedDate(LocalDate.parse(calendarSelectedDate, viewModel.dateFormatter))
                    },
                    modifier = Modifier.padding(bottom = 30.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AppTheme.colorScheme.background,
                        contentColor = AppTheme.colorScheme.primary,
                    ),
                    shape = AppTheme.shape.button,
                    border = BorderStroke(2.dp, AppTheme.colorScheme.activeSliderTrack)
                ) {
                    Text(text = "Izberi datum", style = AppTheme.typography.labelLarge)
                }
            }
        }
    } else {

        Column(modifier = Modifier
            .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ni ustreznih podatkov za prikaz koledarja.",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

private fun displayMonthNameLocale(yearMonth: YearMonth): String {
    return "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale("sl"))} ${yearMonth.year}"
}

private fun displayWeekNameLocale(dayOfWeek: DayOfWeek): String {
    return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("sl")).uppercase().substring(0, 3)
}

private fun timestampToLocalDate(seconds: Long): LocalDate {
    return Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).toLocalDate()
}

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = displayWeekNameLocale(dayOfWeek),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colorScheme.activeSliderTrack
            )
        }
    }
}

@Composable
private fun Day(day: CalendarDay, calendarSelectedDate: String, viewModel: LekcionarViewModel, onSelectedDateChange: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(6.dp)
            .clip(CircleShape)
            .background(
                color = if (viewModel.dateFormatter
                        .format(day.date)
                        .equals(calendarSelectedDate)
                ) AppTheme.colorScheme.primary else Color.Transparent
            )
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                indication = rememberRipple(
                    color = AppTheme.colorScheme.background,
                    bounded = false
                ),
                interactionSource = interactionSource,
                onClick = { onSelectedDateChange(viewModel.dateFormatter.format(day.date)) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when (day.position) {
            DayPosition.MonthDate -> if (viewModel.dateFormatter.format(day.date).equals(calendarSelectedDate)) AppTheme.colorScheme.background else AppTheme.colorScheme.secondary
            DayPosition.InDate -> AppTheme.colorScheme.inactiveSliderTrack
            DayPosition.OutDate -> AppTheme.colorScheme.inactiveSliderTrack

        }
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 14.sp,
            style = AppTheme.typography.labelLarge
        )
    }
}

@Composable
private fun SimpleCalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Row(
        modifier = modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarNavigationIcon(
            icon = Icons.Rounded.KeyboardArrowLeft,
            contentDescription = "Previous",
            onClick = goToPrevious,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .testTag("MonthTitle"),
            text = displayMonthNameLocale(currentMonth),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            style = AppTheme.typography.titleNormal,
            color = AppTheme.colorScheme.secondary
        )
        CalendarNavigationIcon(
            icon = Icons.Rounded.KeyboardArrowRight,
            contentDescription = "Next",
            onClick = goToNext,
        )
    }
}

@Composable
private fun CalendarNavigationIcon(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) = Box(
    modifier = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .clickable(role = Role.Button, onClick = onClick),
) {
    Icon(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .align(Alignment.Center),
        imageVector = icon,
        contentDescription = contentDescription,
        tint = AppTheme.colorScheme.primary
    )
}


@Composable
private fun rememberFirstMostVisibleMonth(
    state: CalendarState,
    viewportPercent: Float = 50f,
): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleMonth(viewportPercent) }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

private fun CalendarLayoutInfo.firstMostVisibleMonth(viewportPercent: Float = 50f): CalendarMonth? {
    return if (visibleMonthsInfo.isEmpty()) {
        null
    } else {
        val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
        visibleMonthsInfo.firstOrNull { itemInfo ->
            if (itemInfo.offset < 0) {
                itemInfo.offset + itemInfo.size >= viewportSize
            } else {
                itemInfo.size - itemInfo.offset >= viewportSize
            }
        }?.month
    }
}

