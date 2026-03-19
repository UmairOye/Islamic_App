package com.ub.islamicapp.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ub.islamicapp.R
import com.ub.islamicapp.screens.calendar.model.UpcomingEvent
import com.ub.islamicapp.theme.ThemeDarkGreenIcon
import com.ub.islamicapp.theme.ThemeLightGreenBg

@Composable
fun UpcomingEventsList(events: List<UpcomingEvent>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.cal_key_islamic_dates),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0F172A)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ThemeLightGreenBg)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.cal_upcoming),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ThemeDarkGreenIcon
                )
            }
        }

        events.forEachIndexed { index, event ->
            CalendarEventItem(event = event, isPrimary = index == 0)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
