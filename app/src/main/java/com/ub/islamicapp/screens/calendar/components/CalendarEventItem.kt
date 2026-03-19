package com.ub.islamicapp.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ub.islamicapp.screens.calendar.model.UpcomingEvent
import com.ub.islamicapp.theme.ThemeDarkGreenIcon
import com.ub.islamicapp.theme.ThemeLightGreenBg

@Composable
fun CalendarEventItem(event: UpcomingEvent, isPrimary: Boolean) {
    val dateBg = if (isPrimary) ThemeDarkGreenIcon else ThemeLightGreenBg
    val dateTextColor = if (isPrimary) Color.White else ThemeDarkGreenIcon

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(dateBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = event.day,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = dateTextColor
                )
                Text(
                    text = event.month.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                    color = if (isPrimary) Color.White.copy(alpha = 0.8f) else ThemeDarkGreenIcon.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0F172A)
            )
            Text(
                text = event.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }
        
        Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8)
        )
    }
}
