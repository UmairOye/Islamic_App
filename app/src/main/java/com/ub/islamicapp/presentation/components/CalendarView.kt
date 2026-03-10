package com.ub.islamicapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ub.islamicapp.theme.PrimaryGreen
import com.ub.islamicapp.utils.CalendarUtils
import com.ub.islamicapp.utils.MonthData
import com.ub.islamicapp.utils.IslamicEventsProvider
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CalendarView(
    isHijri: Boolean,
    modifier: Modifier = Modifier
) {
    var monthOffset by remember { mutableIntStateOf(0) }
    var selectedDay by remember { mutableIntStateOf(-1) }

    val monthData = if (isHijri) {
        CalendarUtils.getHijriMonthData(monthOffset)
    } else {
        CalendarUtils.getGregorianMonthData(monthOffset)
    }

    // Automatically select "today" if in current month, otherwise 1st of month
    LaunchedEffect(monthData) {
        val todayCell = monthData.days.find { it.isToday }
        if (todayCell != null) {
            selectedDay = todayCell.dayOfMonth
        } else {
            selectedDay = 1
        }
    }

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        // Header
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

        // Days of Week Header
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
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

        // Calendar Grid
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

        // Event Card (If Hijri and selected)
        if (isHijri && selectedDay > 0) {
            Spacer(modifier = Modifier.height(32.dp))
            val eventDesc = IslamicEventsProvider.getEventForDate(monthData.monthIndex, selectedDay)

            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Historical Event - $selectedDay ${monthData.monthName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryGreen)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = eventDesc ?: "No major historical events recorded on this date.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
