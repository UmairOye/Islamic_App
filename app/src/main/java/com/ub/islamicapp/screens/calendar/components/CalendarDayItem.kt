package com.ub.islamicapp.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ub.islamicapp.theme.ThemeDarkGreenIcon
import com.ub.islamicapp.utils.CalendarDay

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

    val bgColor = if (isSelected) ThemeDarkGreenIcon else Color.Transparent
    val txtColor = if (isSelected) Color.White else Color(0xFF0F172A)
    val subColor = if (isSelected) Color.White.copy(alpha = 0.7f) else Color(0xFF94A3B8)

    val cornerRadius = if (isSelected) 8.dp else 16.dp

    Box(
        modifier = Modifier
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(cornerRadius))
            .background(bgColor)
            .clickable { onDayClick() }
            .padding(vertical = 4.dp),
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
            if (isSelected && day.hasEvent) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.White))
            }
        }
    }
}
