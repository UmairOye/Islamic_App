package com.ub.islamicapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ub.islamicapp.theme.PrimaryGreen
import com.ub.islamicapp.utils.CalendarUtils
import androidx.compose.foundation.BorderStroke
import com.ub.islamicapp.utils.IslamicEventsProvider
import com.ub.islamicapp.utils.MonthData

@Composable
fun CalendarView(
    isHijri: Boolean,
    modifier: Modifier = Modifier
) {
    if (isHijri) {
        HijriCalendarView(modifier = modifier)
    } else {
        GregorianCalendarView(modifier = modifier)
    }
}

@Composable
fun HijriCalendarView(modifier: Modifier = Modifier) {
    var monthOffset by remember { mutableIntStateOf(0) }
    var selectedDay by remember { mutableIntStateOf(-1) }

    val monthData = CalendarUtils.getHijriMonthData(monthOffset)

    LaunchedEffect(monthData) {
        val todayCell = monthData.days.find { it.isToday }
        if (todayCell != null) {
            selectedDay = todayCell.dayOfMonth
        } else {
            selectedDay = 1
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${monthData.monthName} ${monthData.year}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                )
                val gregorianDateText = if (monthData.gregorianMonthName != null && monthData.gregorianYear != null) {
                    "${monthData.gregorianMonthName} ${monthData.gregorianYear}"
                } else {
                    "Gregorian Date"
                }
                Text(
                    text = gregorianDateText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { monthOffset-- },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.ChevronLeft, contentDescription = "Previous Month", tint = Color.DarkGray)
                }
                IconButton(
                    onClick = { monthOffset++ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = "Next Month", tint = Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Days of Week
        val daysOfWeek = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                val textColor = Color.Gray
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = textColor,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false
            ) {
                items(monthData.days) { day ->
                    if (day.dayOfMonth > 0) {
                        val isSelected = (day.dayOfMonth == selectedDay)
                        val isToday = day.isToday
                        val backgroundColor = if (isSelected) PrimaryGreen else if (isToday) PrimaryGreen.copy(alpha = 0.1f) else Color.Transparent
                        val mainTextColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                        val subTextColor = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(backgroundColor)
                                .clickable { selectedDay = day.dayOfMonth },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    color = mainTextColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = day.gregorianDay?.toString() ?: "",
                                    color = subTextColor,
                                    fontSize = 10.sp
                                )
                            }
                            // Dot for today if not selected
                            if (isToday && !isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 6.dp)
                                        .size(4.dp)
                                        .background(PrimaryGreen, CircleShape)
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 6.dp)
                                        .size(4.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.aspectRatio(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Key Islamic Dates Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Key Islamic Dates",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Surface(
                color = PrimaryGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "Upcoming",
                    color = PrimaryGreen,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display dynamic upcoming events (up to 3 within the next month)
        // Default to day 1 if we're viewing a future/past month to see its events,
        // or today's date if viewing the current month.
        val todayCell = monthData.days.find { it.isToday }
        val startDayForEvents = todayCell?.dayOfMonth ?: 1
        val upcomingEvents = IslamicEventsProvider.getUpcomingEvents(monthData.monthIndex, startDayForEvents, 3)

        val hijriMonthNames = arrayOf(
            "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
            "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            upcomingEvents.forEachIndexed { index, eventTriple ->
                val (eventDay, eventMonthIdx, eventText) = eventTriple
                val dayStr = String.format("%02d", eventDay)
                val monthStr = hijriMonthNames[eventMonthIdx].take(8).uppercase()

                val parts = eventText.split(" - ", limit = 2)
                val title = if (parts.size > 1) parts[0] else "Historical Event"
                val desc = if (parts.size > 1) parts[1] else eventText

                val isFirst = index == 0
                val iconBgColor = if (isFirst) PrimaryGreen else PrimaryGreen.copy(alpha = 0.2f)
                val iconTextColor = if (isFirst) Color.White else PrimaryGreen

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.1f)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date Icon
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(iconBgColor, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayStr,
                                    color = iconTextColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = monthStr,
                                    color = iconTextColor,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 8.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Texts
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                            Text(
                                text = desc,
                                color = Color.Gray,
                                fontSize = 14.sp,
                                maxLines = 2
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = "Details",
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GregorianCalendarView(modifier: Modifier = Modifier) {
    var monthOffset by remember { mutableIntStateOf(0) }
    var selectedDay by remember { mutableIntStateOf(-1) }

    val monthData = CalendarUtils.getGregorianMonthData(monthOffset)

    LaunchedEffect(monthData) {
        val todayCell = monthData.days.find { it.isToday }
        if (todayCell != null) {
            selectedDay = todayCell.dayOfMonth
        } else {
            selectedDay = 1
        }
    }

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { monthOffset-- }) {
                Icon(imageVector = Icons.Rounded.ChevronLeft, contentDescription = "Previous Month")
            }
            Text(
                text = "${monthData.monthName} ${monthData.year}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = { monthOffset++ }) {
                Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(monthData.days) { day ->
                val isSelected = (day.dayOfMonth == selectedDay)
                val bgColor = if (isSelected) PrimaryGreen else if (day.isToday) PrimaryGreen.copy(alpha = 0.3f) else if (day.dayOfMonth > 0) Color.LightGray.copy(alpha = 0.2f) else Color.Transparent
                val txtColor = if (isSelected) Color.White else if (day.isToday) PrimaryGreen else Color.Black

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(bgColor)
                        .clickable(enabled = day.dayOfMonth > 0) {
                            selectedDay = day.dayOfMonth
                        }
                ) {
                    if (day.dayOfMonth > 0) {
                        Text(
                            text = day.dayOfMonth.toString(),
                            color = txtColor,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal)
                        )
                    }
                }
            }
        }
    }
}
