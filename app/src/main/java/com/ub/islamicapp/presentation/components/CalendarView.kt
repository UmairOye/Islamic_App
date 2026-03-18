package com.ub.islamicapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ub.islamicapp.R
import com.ub.islamicapp.theme.PrimaryGreen
import com.ub.islamicapp.utils.CalendarUtils
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
        modifier = modifier.fillMaxWidth().verticalScroll(androidx.compose.foundation.rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF8B5A2B)))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val gregorianDateText = if (monthData.gregorianDayOfMonth != null && monthData.gregorianMonthName != null && monthData.gregorianYear != null) {
                        "${monthData.gregorianDayOfMonth} ${monthData.gregorianMonthName} ${monthData.gregorianYear}"
                    } else {
                        "Gregorian Date"
                    }
                    Text(
                        text = gregorianDateText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$selectedDay ${monthData.monthName} ${monthData.year}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { monthOffset-- }) {
                Icon(imageVector = Icons.Rounded.ChevronLeft, contentDescription = "Previous Month", tint = Color.Black)
            }
            Text(
                text = "${monthData.monthName} ${monthData.year}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF6B4226)),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            IconButton(onClick = { monthOffset++ }) {
                Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = "Next Month", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                val textColor = when (day) {
                    "Fri" -> PrimaryGreen
                    "Sun" -> Color.Red
                    else -> Color.DarkGray
                }
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().height(350.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false
            ) {
                items(monthData.days) { day ->
                    if (day.dayOfMonth > 0) {
                        val isSelected = (day.dayOfMonth == selectedDay)

                        val mainTextColor = when {
                            monthData.days.indexOf(day) % 7 == 4 -> PrimaryGreen
                            monthData.days.indexOf(day) % 7 == 6 -> Color.Red
                            else -> Color.Black
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(0.8f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryGreen else Color.Transparent)
                                .clickable { selectedDay = day.dayOfMonth },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    color = if (isSelected) Color.White else mainTextColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = day.gregorianDay?.toString() ?: "",
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.aspectRatio(0.8f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDay > 0) {
            val eventDesc = IslamicEventsProvider.getEventForDate(monthData.monthIndex, selectedDay)
            if (!eventDesc.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Historical Event - $selectedDay ${monthData.monthName}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryGreen)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = eventDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        Text(
            text = "Hijri\nCalendar\n${monthData.year}",
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
            lineHeight = 60.sp
        )
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

    Column(modifier = modifier.fillMaxWidth().verticalScroll(androidx.compose.foundation.rememberScrollState()).padding(16.dp)) {
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
            modifier = Modifier.fillMaxWidth().height(350.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
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
