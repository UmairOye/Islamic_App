package com.ub.islamicapp.domain.model

data class PrayerTime(
    val name: String,
    val time: String,
    val isCompleted: Boolean = false
)

data class TimeRemaining(
    val prayerName: String,
    val hours: Int,
    val minutes: Int
)

data class HijriDate(
    val day: Int,
    val monthIndex: Int,
    val year: Int,
    val fallbackString: String? = null
)

data class PrayerTimes(
    val locationName: String,
    val hijriDate: HijriDate,
    val currentTime: String,
    val timeRemaining: TimeRemaining?,
    val prayers: List<PrayerTime>,
    val nextPrayer: String
)
