package com.ub.islamicapp.domain.usecase

import com.ub.islamicapp.domain.model.PrayerTimes
import com.ub.islamicapp.domain.repository.PrayerRepository
import javax.inject.Inject

class GetPrayerTimesUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<PrayerTimes> {
        return try {
            Result.success(repository.getPrayerTimes(latitude, longitude))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
