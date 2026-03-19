package com.ub.islamicapp.screens.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ub.islamicapp.utils.CalendarUtils
import com.ub.islamicapp.utils.IslamicEventsProvider
import com.ub.islamicapp.screens.calendar.model.UpcomingEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun init(isHijri: Boolean) {
        _uiState.update { it.copy(isHijri = isHijri, currentMonthOffset = 0) }
        loadMonthData(0)
    }

    fun changeMonth(offsetDelta: Int) {
        val newOffset = _uiState.value.currentMonthOffset + offsetDelta
        _uiState.update { it.copy(currentMonthOffset = newOffset) }
        loadMonthData(newOffset)
    }

    fun selectDay(day: Int) {
        _uiState.update { it.copy(selectedDay = day) }
        viewModelScope.launch {
            val upcomingEvents = fetchUpcomingEvents(_uiState.value.isHijri, _uiState.value.monthData.monthIndex, day)
            _uiState.update { it.copy(upcomingEvents = upcomingEvents) }
        }
    }

    private fun loadMonthData(offset: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val isHijri = _uiState.value.isHijri
            val monthData = if (isHijri) {
                CalendarUtils.getHijriMonthData(offset)
            } else {
                CalendarUtils.getGregorianMonthData(offset)
            }

            val todayCell = monthData.days.find { it.isToday }
            val selectedDay = todayCell?.dayOfMonth ?: -1

            val upcomingEvents = fetchUpcomingEvents(isHijri, monthData.monthIndex, if (selectedDay > 0) selectedDay else 1)

            _uiState.update {
                it.copy(
                    monthData = monthData,
                    selectedDay = selectedDay,
                    upcomingEvents = upcomingEvents
                )
            }
        }
    }

    private fun fetchUpcomingEvents(isHijri: Boolean, currentMonthIndex: Int, startDay: Int): List<UpcomingEvent> {
        val events = mutableListOf<UpcomingEvent>()
        if (!isHijri) return events

        val maxDaysToScan = 10
        var daysScanned = 0
        var currentScanDay = startDay
        var currentScanMonth = currentMonthIndex

        while (daysScanned < maxDaysToScan) {
            val desc = IslamicEventsProvider.getEventForDate(currentScanMonth, currentScanDay)
            if (!desc.isNullOrBlank()) {
                val shortTitle = desc.split(" ").take(3).joinToString(" ")
                events.add(UpcomingEvent(
                    day = String.format("%02d", currentScanDay),
                    month = CalendarUtils.getHijriMonthName(currentScanMonth),
                    title = shortTitle,
                    subtitle = desc
                ))
            }

            daysScanned++
            currentScanDay++

            // Hijri months alternate between 29 and 30 days, but for simplicity in this logic
            // Assuming 30 as a safe boundary.
            if (currentScanDay > 30) {
                currentScanDay = 1
                currentScanMonth = (currentScanMonth + 1) % 12
            }
        }

        return events
    }
}
