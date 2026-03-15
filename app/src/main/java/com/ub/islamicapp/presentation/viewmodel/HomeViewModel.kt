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
            // Only fetch if we haven't successfully fetched today, or if we want to ensure it's loaded.
            // Since this app targets caching, we should check if prayers are already loaded
            if (_uiState.value.prayerTimes.isNotEmpty() && _uiState.value.prayerTimes.first().time != "--:--") {
                return@launch
            }

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
                // Update time locally without relying on location fetching/network to prevent sticking
                val cal = java.util.Calendar.getInstance()
                val currentTimeStr = java.text.SimpleDateFormat("hh:mm", java.util.Locale.getDefault()).format(cal.time)

                val currentHour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                val currentMin = cal.get(java.util.Calendar.MINUTE)
                val currentMillis = cal.timeInMillis

                var newTimeRemaining = _uiState.value.timeRemaining
                var nextPrayerStr = _uiState.value.nextPrayer
                var foundNext = false

                // Recalculate remaining time and completed status based on previously fetched prayer times
                val updatedPrayers = _uiState.value.prayerTimes.map { prayer ->
                    if (prayer.time == "--:--") return@map prayer
                    // The time stored in the state is now what was returned from the UseCase/Repository, which is 24-hour format
                    val parts = prayer.time.split(":")
                    val pHour = parts[0].toInt()
                    val pMin = parts[1].toInt()

                    val isCompleted = (currentHour > pHour) || (currentHour == pHour && currentMin >= pMin)

                    if (!isCompleted && !foundNext) {
                        foundNext = true
                        nextPrayerStr = prayer.name

                        val pCal = java.util.Calendar.getInstance()
                        pCal.set(java.util.Calendar.HOUR_OF_DAY, pHour)
                        pCal.set(java.util.Calendar.MINUTE, pMin)
                        pCal.set(java.util.Calendar.SECOND, 0)

                        val diff = pCal.timeInMillis - currentMillis
                        val hoursRem = diff / (1000 * 60 * 60)
                        val minsRem = (diff / (1000 * 60)) % 60
                        newTimeRemaining = "${prayer.name} $hoursRem hour $minsRem min left"
                    }

                    prayer.copy(isCompleted = isCompleted)
                }

                if (!foundNext && _uiState.value.prayerTimes.isNotEmpty() && _uiState.value.prayerTimes.first().time != "--:--") {
                    val firstPrayer = _uiState.value.prayerTimes.first()
                    nextPrayerStr = firstPrayer.name
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
                    newTimeRemaining = "${firstPrayer.name} $hoursRem hour $minsRem min left"
                }

                // Format the updated prayer times to 12-hour for UI display only (we keep 24h format in the state for math, but we can't if we use the same state variable, let's keep 24h in state and format in UI)
                // Wait, if we keep 24-hour in the state, the UI will just display it. Let's fix the UI component to format it.

                _uiState.value = _uiState.value.copy(
                    currentTime = currentTimeStr,
                    timeRemaining = newTimeRemaining,
                    nextPrayer = nextPrayerStr,
                    prayerTimes = updatedPrayers
                )

                delay(10_000) // Poll every 10 seconds for smooth update locally
            }
        }
    }
}
