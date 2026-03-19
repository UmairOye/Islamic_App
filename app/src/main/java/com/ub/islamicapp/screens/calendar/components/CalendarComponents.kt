package com.ub.islamicapp.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ub.islamicapp.screens.calendar.viewmodel.UpcomingEvent
import com.ub.islamicapp.theme.PrimaryGreen
import com.ub.islamicapp.utils.CalendarDay

@Composable
fun CalendarHeader(
    title: String,
    subtitle: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.Rounded.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = Color(0xFF1E293B)
                )
            }
            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = "Next Month",
                    tint = Color(0xFF1E293B)
                )
            }
        }
    }
}

@Composable
fun CalendarGrid(
    days: List<CalendarDay>,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    val weekDays = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { dayName ->
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
            userScrollEnabled = false
        ) {
            items(days) { day ->
                CalendarDayItem(
                    day = day,
                    isSelected = day.dayOfMonth == selectedDay,
                    onDayClick = { onDaySelected(day.dayOfMonth) }
                )
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    day: CalendarDay,
    isSelected: Boolean,
    onDayClick: () -> Unit
) {
    if (day.dayOfMonth <= 0) {
        Box(modifier = Modifier.aspectRatio(1f).padding(4.dp))
        return
    }

    val bgColor = if (isSelected) PrimaryGreen else Color.Transparent
    val txtColor = if (isSelected) Color.White else if (day.isToday) PrimaryGreen else Color(0xFF1E293B)
    val subColor = if (isSelected) Color.White.copy(alpha = 0.7f) else Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onDayClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal
                ),
                color = txtColor
            )
            if (day.gregorianDay != null) {
                Text(
                    text = day.gregorianDay?.toString() ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = subColor
                )
            }
        }
    }
}

@Composable
fun UpcomingEventsList(events: List<UpcomingEvent>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Key Islamic Dates",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1E293B)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(PrimaryGreen.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Upcoming",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimaryGreen
                )
            }
        }

        events.forEach { event ->
            EventItem(event)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun EventItem(event: UpcomingEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1F5F9))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryGreen),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = event.day,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = event.month.take(3).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1E293B)
            )
            Text(
                text = event.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }
    }
}
