package com.ub.islamicapp.domain.repository

import com.ub.islamicapp.domain.model.PrayerTimes
import kotlinx.coroutines.flow.Flow

interface PrayerRepository {
    suspend fun getPrayerTimes(latitude: Double, longitude: Double): PrayerTimes
    fun getLocationName(latitude: Double, longitude: Double): Flow<String>
}
