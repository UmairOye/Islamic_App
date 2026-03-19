package com.ub.islamicapp.domain.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
    suspend fun saveManualLocation(lat: Double, lng: Double)
}
