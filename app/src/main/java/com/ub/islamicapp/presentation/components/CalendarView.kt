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

@Composable
fun CalendarView(
    isHijri: Boolean,
    modifier: Modifier = Modifier
) {
    var monthOffset by remember { mutableIntStateOf(0) }

    val monthData = if (isHijri) {
        CalendarUtils.getHijriMonthData(monthOffset)
    } else {
        CalendarUtils.getGregorianMonthData(monthOffset)
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(
                            color = if (day.isToday) PrimaryGreen else if (day.dayOfMonth > 0) Color.LightGray.copy(alpha = 0.2f) else Color.Transparent
                        )
                        .clickable(enabled = day.dayOfMonth > 0) { /* Handle selection if needed */ }
                ) {
                    if (day.dayOfMonth > 0) {
                        Text(
                            text = day.dayOfMonth.toString(),
                            color = if (day.isToday) Color.White else Color.Black,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal)
                        )
                    }
                }
            }
        }
    }
}
