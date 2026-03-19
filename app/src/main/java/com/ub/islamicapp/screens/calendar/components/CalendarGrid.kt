package com.ub.islamicapp.screens.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ub.islamicapp.R
import com.ub.islamicapp.utils.CalendarDay

@Composable
fun CalendarGrid(
    days: List<CalendarDay>,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    val weekDays = listOf(
        stringResource(id = R.string.cal_sun),
        stringResource(id = R.string.cal_mon),
        stringResource(id = R.string.cal_tue),
        stringResource(id = R.string.cal_wed),
        stringResource(id = R.string.cal_thu),
        stringResource(id = R.string.cal_fri),
        stringResource(id = R.string.cal_sat)
    )

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
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
