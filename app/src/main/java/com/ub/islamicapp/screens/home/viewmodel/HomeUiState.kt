package com.ub.islamicapp.screens.home.viewmodel

import com.ub.islamicapp.domain.model.HijriDate
import com.ub.islamicapp.domain.model.PrayerTime
import com.ub.islamicapp.domain.model.TimeRemaining
import com.ub.islamicapp.data.models.RecentCityEntity

data class HomeUiState(
    val location: String = "--",
    val currentTime: String = "--",
    val hijriDate: HijriDate? = null,
    val timeRemaining: TimeRemaining? = null,
    val prayerTimes: List<PrayerTime> = emptyList(),
    val nextPrayer: String = "--",
    val lastReadSurah: String = "الفاتحة",
    val lastReadVerse: String = "Ayah no. 1",
    val isLoading: Boolean = false,
    val error: String? = null,
    val recentCities: List<RecentCityEntity> = emptyList()
)
