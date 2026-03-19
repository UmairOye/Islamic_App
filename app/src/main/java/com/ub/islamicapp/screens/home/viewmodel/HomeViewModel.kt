package com.ub.islamicapp.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ub.islamicapp.domain.location.LocationTracker
import com.ub.islamicapp.domain.model.HijriDate
import com.ub.islamicapp.domain.model.TimeRemaining
import com.ub.islamicapp.data.datasource.RecentCityDao
import com.ub.islamicapp.data.models.RecentCityEntity
import com.ub.islamicapp.domain.usecase.GetPrayerTimesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationTracker: LocationTracker,
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val recentCityDao: RecentCityDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        startPollingTime()
        fetchRecentCities()
    }

    private fun fetchRecentCities() {
        viewModelScope.launch {
            recentCityDao.getRecentCities().collect { cities ->
                _uiState.update { it.copy(recentCities = cities) }
            }
        }
    }

    fun saveLocationAndFetchPrayers(lat: Double, lng: Double, cityName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            locationTracker.saveManualLocation(lat, lng)
            recentCityDao.insertRecentCity(RecentCityEntity(cityName, lat, lng, System.currentTimeMillis()))
            val result = getPrayerTimesUseCase(lat, lng)
            result.fold(
                onSuccess = { prayerTimes ->
                    _uiState.value = _uiState.value.copy(
                        location = cityName,
                        currentTime = prayerTimes.currentTime,
                        hijriDate = prayerTimes.hijriDate,
                        timeRemaining = prayerTimes.timeRemaining,
                        prayerTimes = prayerTimes.prayers,
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
        }
    }

    fun updateLocationAndPrayers(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!forceRefresh && _uiState.value.prayerTimes.isNotEmpty() && _uiState.value.prayerTimes.first().time != "--:--") {
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
                            prayerTimes = prayerTimes.prayers,
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
                val currentTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
                val hijriObj = try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        val islamicCalendar = android.icu.util.IslamicCalendar()
                        val hDay = islamicCalendar.get(android.icu.util.IslamicCalendar.DAY_OF_MONTH)
                        val hMonth = islamicCalendar.get(android.icu.util.IslamicCalendar.MONTH)
                        val hYear = islamicCalendar.get(android.icu.util.IslamicCalendar.YEAR)
                        HijriDate(hDay, hMonth, hYear, null)
                    } else HijriDate(0, 0, 0, "Unknown Date")
                } catch (e: Exception) { HijriDate(0, 0, 0, "Unknown Date") }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentTime = currentTimeStr,
                    hijriDate = hijriObj,
                    error = "NO_LOCATION"
                )
            }
        }
    }

    private fun startPollingTime() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                val cal = Calendar.getInstance()
                val currentTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)

                val currentHour = cal.get(Calendar.HOUR_OF_DAY)
                val currentMin = cal.get(Calendar.MINUTE)
                val currentMillis = cal.timeInMillis

                var newTimeRemaining = _uiState.value.timeRemaining
                var nextPrayerStr = _uiState.value.nextPrayer
                var foundNext = false

                val updatedPrayers = _uiState.value.prayerTimes.map { prayer ->
                    if (prayer.time == "--:--") return@map prayer

                    val parts = prayer.time.split(":")
                    val pHour = parts[0].toInt()
                    val pMin = parts[1].toInt()

                    val isCompleted = (currentHour > pHour) || (currentHour == pHour && currentMin >= pMin)

                    if (!isCompleted && !foundNext && prayer.name != "Sunrise") {
                        foundNext = true
                        nextPrayerStr = prayer.name

                        val pCal = Calendar.getInstance()
                        pCal.set(Calendar.HOUR_OF_DAY, pHour)
                        pCal.set(Calendar.MINUTE, pMin)
                        pCal.set(Calendar.SECOND, 0)

                        val diff = pCal.timeInMillis - currentMillis
                        val hoursRem = (diff / (1000 * 60 * 60)).toInt()
                        val minsRem = ((diff / (1000 * 60)) % 60).toInt()
                        newTimeRemaining = TimeRemaining(prayer.name, hoursRem, minsRem)
                    }

                    prayer.copy(isCompleted = isCompleted)
                }

                if (!foundNext && _uiState.value.prayerTimes.isNotEmpty() && _uiState.value.prayerTimes.first().time != "--:--") {
                    val firstPrayer = _uiState.value.prayerTimes.first()
                    nextPrayerStr = firstPrayer.name
                    val parts = firstPrayer.time.split(":")
                    val pHour = parts[0].toInt()
                    val pMin = parts[1].toInt()

                    val pCal = Calendar.getInstance()
                    pCal.add(Calendar.DAY_OF_YEAR, 1)
                    pCal.set(Calendar.HOUR_OF_DAY, pHour)
                    pCal.set(Calendar.MINUTE, pMin)
                    pCal.set(Calendar.SECOND, 0)

                    val diff = pCal.timeInMillis - currentMillis
                    val hoursRem = (diff / (1000 * 60 * 60)).toInt()
                    val minsRem = ((diff / (1000 * 60)) % 60).toInt()
                    newTimeRemaining = TimeRemaining(firstPrayer.name, hoursRem, minsRem)
                }

                _uiState.value = _uiState.value.copy(
                    currentTime = currentTimeStr,
                    timeRemaining = newTimeRemaining,
                    nextPrayer = nextPrayerStr,
                    prayerTimes = updatedPrayers
                )

                delay(10_000)
            }
        }
    }
}
