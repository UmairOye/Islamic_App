package com.ub.islamicapp.screens.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ub.islamicapp.R
import com.ub.islamicapp.theme.ThemeDarkGreenIcon

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
                    color = ThemeDarkGreenIcon
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
                    contentDescription = stringResource(id = R.string.cal_prev_month),
                    tint = Color(0xFF1E293B)
                )
            }
            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = stringResource(id = R.string.cal_next_month),
                    tint = Color(0xFF1E293B)
                )
            }
        }
    }
}
