package com.ub.islamicapp.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ub.islamicapp.screens.home.viewmodel.PrayerTime

@Composable
fun PrayerTimesRow(
    prayers: List<PrayerTime>,
    nextPrayer: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        prayers.forEach { prayer ->
            val isNext = prayer.name == nextPrayer
            PrayerItem(prayer = prayer, isNext = isNext)
        }
    }
}
