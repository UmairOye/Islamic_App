package com.ub.islamicapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ModeNight
import androidx.compose.material.icons.rounded.WbCloudy
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ub.islamicapp.presentation.state.PrayerTime

@Composable
fun PrayerItem(
    prayer: PrayerTime,
    isNext: Boolean,
    modifier: Modifier = Modifier
) {
    val icon = when (prayer.name.lowercase()) {
        "fajr" -> Icons.Rounded.WbCloudy
        "dzuhr", "dhuhr" -> Icons.Rounded.WbSunny
        "asr" -> Icons.Rounded.WbSunny
        "maghrib" -> Icons.Rounded.WbSunny
        else -> Icons.Rounded.ModeNight
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = prayer.name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
            ),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = prayer.name,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = prayer.time,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
            ),
            color = Color.White
        )
    }
}
