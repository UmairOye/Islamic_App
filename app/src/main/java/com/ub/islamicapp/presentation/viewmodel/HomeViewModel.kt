package com.ub.islamicapp.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ub.islamicapp.domain.location.LocationTracker
import com.ub.islamicapp.domain.usecase.GetPrayerTimesUseCase
import com.ub.islamicapp.presentation.state.HomeUiState
import com.ub.islamicapp.presentation.state.PrayerTime
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationTracker: LocationTracker,
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        // Start polling immediately for system time updates even if location is not resolved
        startPollingTime()
    }

    fun updateLocationAndPrayers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                val result = getPrayerTimesUseCase(location.latitude, location.longitude)
                result.fold(
                    onSuccess = { prayerTimes ->
                        _uiState.value = _uiState.value.copy(
                            location = prayerTimes.locationName,
                            currentTime = prayerTimes.currentTime,
                            hijriDate = prayerTimes.hijriDate,
                            timeRemaining = prayerTimes.timeRemaining,
                            prayerTimes = prayerTimes.prayers.map { 
                                PrayerTime(it.name, it.time, it.isCompleted) 
                            },
                            nextPrayer = prayerTimes.nextPrayer,
                            isLoading = false
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                )
            } else {
                // If location doesn't exist at all, update the current system time anyway
                val currentTimeStr = java.text.SimpleDateFormat("hh:mm", java.util.Locale.getDefault()).format(java.util.Calendar.getInstance().time)
                val hijriStr = try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        val islamicCalendar = android.icu.util.IslamicCalendar()
                        val hDay = islamicCalendar.get(android.icu.util.IslamicCalendar.DAY_OF_MONTH)
                        val hMonth = islamicCalendar.get(android.icu.util.IslamicCalendar.MONTH)
                        val hYear = islamicCalendar.get(android.icu.util.IslamicCalendar.YEAR)
                        val monthNames = arrayOf("Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani", "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah")
                        "$hDay ${monthNames[hMonth]} $hYear H"
                    } else "Unknown Date"
                } catch (e: Exception) { "Unknown Date" }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentTime = currentTimeStr,
                    hijriDate = hijriStr,
                    error = "NO_LOCATION" // Specific error code to show placeholder UI
                )
            }
        }
    }

    private fun startPollingTime() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {

                val location = locationTracker.getCurrentLocation()
                if (location != null) {
                    val result = getPrayerTimesUseCase(location.latitude, location.longitude)
                    result.fold(
                        onSuccess = { prayerTimes ->
                            _uiState.value = _uiState.value.copy(
                                location = prayerTimes.locationName,
                                currentTime = prayerTimes.currentTime,
                                timeRemaining = prayerTimes.timeRemaining,
                                nextPrayer = prayerTimes.nextPrayer,
                                prayerTimes = prayerTimes.prayers.map {
                                    PrayerTime(it.name, it.time, it.isCompleted)
                                },
                                error = null // Clear any previous error
                            )
                        },
                        onFailure = { /* silently ignore poll failure */ }
                    )
                } else {
                    // Update system time if no location
                    val currentTimeStr = java.text.SimpleDateFormat("hh:mm", java.util.Locale.getDefault()).format(java.util.Calendar.getInstance().time)
                    _uiState.value = _uiState.value.copy(currentTime = currentTimeStr)
                }
                delay(60_000) // Poll every 1 minute
            }
        }
    }
}
