package com.ub.islamicapp.data.repository

import com.ub.islamicapp.domain.model.PrayerTime
import com.ub.islamicapp.domain.model.PrayerTimes
import com.ub.islamicapp.domain.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PrayerRepositoryImpl @Inject constructor() : PrayerRepository {

    override suspend fun getPrayerTimes(latitude: Double, longitude: Double): PrayerTimes {
        // In a real app, this would call an API or use a local calculation library
        // For now, returning mock data as requested for "backend" implementation
        return PrayerTimes(
            locationName = "Current Location", // Will be updated by getLocationName
            hijriDate = "9 Ramadhan 1444 H",
            currentTime = "04:41",
            timeRemaining = "Fajr 3 hour 9 min left",
            prayers = listOf(
                PrayerTime(name = "Fajr", time = "04:41", isCompleted = true),
                PrayerTime(name = "Dzuhr", time = "12:00", isCompleted = true),
                PrayerTime(name = "Asr", time = "15:14", isCompleted = false),
                PrayerTime(name = "Maghrib", time = "18:02", isCompleted = false),
                PrayerTime(name = "Isha", time = "19:11", isCompleted = false)
            ),
            nextPrayer = "Fajr"
        )
    }

    override fun getLocationName(latitude: Double, longitude: Double): Flow<String> = flow {
        // Mock geocoding
        emit("Jakarta, Indonesia")
    }
}
