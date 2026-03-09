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

    fun updateLocationAndPrayers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
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
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unable to get location. Please grant permission."
                )
            }
            startPollingTime()
        }
    }

    private fun startPollingTime() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(60_000) // Poll every 1 minute

                val location = locationTracker.getCurrentLocation()
                if (location != null) {
                    val result = getPrayerTimesUseCase(location.latitude, location.longitude)
                    result.fold(
                        onSuccess = { prayerTimes ->
                            _uiState.value = _uiState.value.copy(
                                currentTime = prayerTimes.currentTime,
                                timeRemaining = prayerTimes.timeRemaining,
                                nextPrayer = prayerTimes.nextPrayer,
                                prayerTimes = prayerTimes.prayers.map {
                                    PrayerTime(it.name, it.time, it.isCompleted)
                                }
                            )
                        },
                        onFailure = { /* silently ignore poll failure */ }
                    )
                }
            }
        }
    }
}
