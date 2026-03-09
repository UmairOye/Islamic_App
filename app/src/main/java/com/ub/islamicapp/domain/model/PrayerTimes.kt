package com.ub.islamicapp.domain.model

data class PrayerTime(
    val name: String,
    val time: String,
    val isCompleted: Boolean = false
)

data class PrayerTimes(
    val locationName: String,
    val hijriDate: String,
    val currentTime: String,
    val timeRemaining: String,
    val prayers: List<PrayerTime>,
    val nextPrayer: String
)
