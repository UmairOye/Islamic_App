package com.ub.islamicapp.presentation.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ub.islamicapp.domain.location.LocationTracker
import com.ub.islamicapp.domain.usecase.GetPrayerTimesUseCase
import com.ub.islamicapp.presentation.state.PrayerTime
import com.ub.islamicapp.presentation.state.QiblaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val locationTracker: LocationTracker,
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private val QIBLA_LATITUDE = 21.422487
    private val QIBLA_LONGITUDE = 39.826206

    private var pollingJob: Job? = null

    init {
        startPollingTime()
    }

    fun onPermissionsResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasLocationPermission = granted)
        if (granted) {
            calculateQiblaDirection()
        }
    }

    fun onSensorsAvailable(available: Boolean) {
        _uiState.value = _uiState.value.copy(hasSensors = available)
    }

    private fun calculateQiblaDirection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingLocation = true)
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                val myLat = Math.toRadians(location.latitude)
                val myLng = Math.toRadians(location.longitude)
                val qiblaLat = Math.toRadians(QIBLA_LATITUDE)
                val qiblaLng = Math.toRadians(QIBLA_LONGITUDE)

                val dLng = qiblaLng - myLng
                val y = sin(dLng) * cos(qiblaLat)
                val x = cos(myLat) * sin(qiblaLat) - sin(myLat) * cos(qiblaLat) * cos(dLng)
                var qibla = Math.toDegrees(atan2(y, x)).toFloat()
                if (qibla < 0) {
                    qibla += 360f
                }

                val result = getPrayerTimesUseCase(location.latitude, location.longitude)
                result.fold(
                    onSuccess = { prayerTimes ->
                        _uiState.value = _uiState.value.copy(
                            qiblaDirection = qibla,
                            isLoadingLocation = false,
                            locationName = prayerTimes.locationName,
                            prayerTimes = prayerTimes.prayers.map {
                                PrayerTime(it.name, it.time, it.isCompleted)
                            },
                            nextPrayerName = prayerTimes.nextPrayer
                        )
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            qiblaDirection = qibla,
                            isLoadingLocation = false
                        )
                    }
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoadingLocation = false)
            }
        }
    }

    private fun startPollingTime() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                val cal = java.util.Calendar.getInstance()
                val currentHour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                val currentMin = cal.get(java.util.Calendar.MINUTE)
                val currentMillis = cal.timeInMillis

                var newTimeRemaining = _uiState.value.timeRemaining
                var nextPrayerStr = _uiState.value.nextPrayerName
                var nextPrayerTimeStr = _uiState.value.nextPrayerTime
                var foundNext = false

                val updatedPrayers = _uiState.value.prayerTimes.map { prayer ->
                    if (prayer.time == "--:--") return@map prayer
                    val parts = prayer.time.split(":")
                    val pHour = parts[0].toInt()
                    val pMin = parts[1].toInt()

                    val isCompleted = (currentHour > pHour) || (currentHour == pHour && currentMin >= pMin)

                    if (!isCompleted && !foundNext) {
                        foundNext = true
                        nextPrayerStr = prayer.name
                        nextPrayerTimeStr = prayer.time

                        val pCal = java.util.Calendar.getInstance()
                        pCal.set(java.util.Calendar.HOUR_OF_DAY, pHour)
                        pCal.set(java.util.Calendar.MINUTE, pMin)
                        pCal.set(java.util.Calendar.SECOND, 0)

                        val diff = pCal.timeInMillis - currentMillis
                        val hoursRem = diff / (1000 * 60 * 60)
                        val minsRem = (diff / (1000 * 60)) % 60

                        val hoursStr = if (hoursRem > 0) "${hoursRem}h " else ""
                        newTimeRemaining = "Starts in $hoursStr${minsRem}m"
                    }

                    prayer.copy(isCompleted = isCompleted)
                }

                if (!foundNext && _uiState.value.prayerTimes.isNotEmpty() && _uiState.value.prayerTimes.first().time != "--:--") {
                    val firstPrayer = _uiState.value.prayerTimes.first()
                    nextPrayerStr = firstPrayer.name
                    nextPrayerTimeStr = firstPrayer.time
                    val parts = firstPrayer.time.split(":")
                    val pHour = parts[0].toInt()
                    val pMin = parts[1].toInt()

                    val pCal = java.util.Calendar.getInstance()
                    pCal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    pCal.set(java.util.Calendar.HOUR_OF_DAY, pHour)
                    pCal.set(java.util.Calendar.MINUTE, pMin)
                    pCal.set(java.util.Calendar.SECOND, 0)

                    val diff = pCal.timeInMillis - currentMillis
                    val hoursRem = diff / (1000 * 60 * 60)
                    val minsRem = (diff / (1000 * 60)) % 60

                    val hoursStr = if (hoursRem > 0) "${hoursRem}h " else ""
                    newTimeRemaining = "Starts in $hoursStr${minsRem}m"
                }

                _uiState.value = _uiState.value.copy(
                    timeRemaining = newTimeRemaining,
                    nextPrayerName = nextPrayerStr,
                    nextPrayerTime = nextPrayerTimeStr,
                    prayerTimes = updatedPrayers
                )

                delay(10_000)
            }
        }
    }
}