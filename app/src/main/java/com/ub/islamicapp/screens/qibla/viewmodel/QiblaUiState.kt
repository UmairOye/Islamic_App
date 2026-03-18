package com.ub.islamicapp.screens.qibla.viewmodel

import com.ub.islamicapp.screens.home.viewmodel.PrayerTime

data class QiblaUiState(
    val qiblaDirection: Float = 0f,
    val hasLocationPermission: Boolean = false,
    val hasSensors: Boolean = true,
    val isLoadingLocation: Boolean = false,
    val locationName: String = "--",
    val nextPrayerName: String = "Fajr",
    val nextPrayerTime: String = "--:--",
    val timeRemaining: String = "--",
    val prayerTimes: List<PrayerTime> = emptyList()
)
