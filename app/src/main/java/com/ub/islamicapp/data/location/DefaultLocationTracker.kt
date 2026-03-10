package com.ub.islamicapp.data.location

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.ub.islamicapp.data.datasource.LocationDao
import com.ub.islamicapp.data.models.LocationEntity
import com.ub.islamicapp.domain.location.LocationTracker
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val locationDao: LocationDao,
    private val application: Application
) : LocationTracker {

    override suspend fun getCurrentLocation(): Location? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val canUseGps = hasAccessCoarseLocationPermission && hasAccessFineLocationPermission && isGpsEnabled

        if (canUseGps) {
            val location = suspendCancellableCoroutine<Location?> { cont ->
                locationClient.lastLocation.apply {
                    if (isComplete) {
                        if (isSuccessful) {
                            cont.resume(result)
                        } else {
                            cont.resume(null)
                        }
                        return@suspendCancellableCoroutine
                    }
                    addOnSuccessListener {
                        cont.resume(it)
                    }
                    addOnFailureListener {
                        cont.resume(null)
                    }
                    addOnCanceledListener {
                        cont.cancel()
                    }
                }
            }

            if (location != null) {
                // Save to Room cache
                locationDao.insertLocation(LocationEntity(latitude = location.latitude, longitude = location.longitude))
                return location
            }
        }

        // Return from cache if GPS fails or is disabled
        val cachedLocation = locationDao.getLastLocation()

        if (cachedLocation != null) {
            return Location("cached").apply {
                latitude = cachedLocation.latitude
                longitude = cachedLocation.longitude
            }
        }

        return null
    }
}
