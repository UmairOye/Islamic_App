package com.ub.islamicapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material.icons.rounded.FiberManualRecord
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FeatureGrid(
    modifier: Modifier = Modifier,
    onNavigateToHijri: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToSalah: () -> Unit = {},
    onNavigateToQibla: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "All Features",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureItem(label = "Quran", icon = Icons.AutoMirrored.Rounded.MenuBook, onClick = { }, modifier = Modifier.weight(1f))
            FeatureItem(label = "Hijri", icon = Icons.Rounded.DateRange, onClick = onNavigateToHijri, modifier = Modifier.weight(1f))
            FeatureItem(label = "Qibla", icon = Icons.Rounded.Explore, onClick = onNavigateToQibla, modifier = Modifier.weight(1f))
            FeatureItem(label = "Tasbeeh", icon = Icons.Rounded.FiberManualRecord, onClick = { }, modifier = Modifier.weight(1f))
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureItem(label = "Calendar", icon = Icons.Rounded.CalendarMonth, onClick = onNavigateToCalendar, modifier = Modifier.weight(1f))
            FeatureItem(label = "Dua", icon = Icons.Rounded.WavingHand, onClick = { }, modifier = Modifier.weight(1f))
            FeatureItem(label = "Hadith", icon = Icons.AutoMirrored.Rounded.MenuBook, onClick = { }, modifier = Modifier.weight(1f))
            FeatureItem(label = "Salah", icon = Icons.Rounded.AccessTime, onClick = onNavigateToSalah, modifier = Modifier.weight(1f))
        }
    }
}
