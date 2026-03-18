package com.ub.islamicapp.screens.home.viewmodel

data class PrayerTime(
    val name: String,
    val time: String,
    val isCompleted: Boolean = false
)

data class HomeUiState(
    val location: String = "--",
    val currentTime: String = "--",
    val hijriDate: String = "--",
    val timeRemaining: String = "--",
    val prayerTimes: List<PrayerTime> = emptyList(),
    val nextPrayer: String = "--",
    val lastReadSurah: String = "الفاتحة",
    val lastReadVerse: String = "Ayah no. 1",
    val isLoading: Boolean = false,
    val error: String? = null
)
