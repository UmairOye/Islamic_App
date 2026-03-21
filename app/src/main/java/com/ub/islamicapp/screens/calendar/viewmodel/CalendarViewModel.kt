package com.ub.islamicapp.screens.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ub.islamicapp.utils.CalendarUtils
import com.ub.islamicapp.utils.IslamicEventsProvider
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
    }

    private fun loadMonthData(offset: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val isHijri = _uiState.value.isHijri
            val monthData = if (isHijri) {
                CalendarUtils.getHijriMonthData(offset)
            } else {
                CalendarUtils.getGregorianMonthData(offset)
            }

            val todayCell = monthData.days.find { it.isToday }
            val selectedDay = todayCell?.dayOfMonth ?: -1

            val upcomingEvents = fetchUpcomingEvents(isHijri, monthData.monthIndex)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    monthData = monthData,
                    selectedDay = selectedDay,
                    upcomingEvents = upcomingEvents
                )
            }
        }
    }

    private fun fetchUpcomingEvents(isHijri: Boolean, currentMonthIndex: Int): List<UpcomingEvent> {
        val events = mutableListOf<UpcomingEvent>()
        if (!isHijri) return events

        val todayCell = _uiState.value.monthData.days.find { it.isToday }
        val currentDay = todayCell?.dayOfMonth ?: 1

        for (day in currentDay..30) {
            val desc = IslamicEventsProvider.getEventForDate(currentMonthIndex, day)
            if (!desc.isNullOrBlank()) {
                val shortTitle = desc.split(" ").take(3).joinToString(" ")
                events.add(UpcomingEvent(
                    day = String.format("%02d", day),
                    month = CalendarUtils.getHijriMonthName(currentMonthIndex),
                    title = shortTitle,
                    subtitle = desc
                ))
            }
        }

        val nextMonthIndex = (currentMonthIndex + 1) % 12
        for (day in 1..30) {
            if (events.size >= 10) break
            val desc = IslamicEventsProvider.getEventForDate(nextMonthIndex, day)
            if (!desc.isNullOrBlank()) {
                val shortTitle = desc.split(" ").take(3).joinToString(" ")
                events.add(UpcomingEvent(
                    day = String.format("%02d", day),
                    month = CalendarUtils.getHijriMonthName(nextMonthIndex),
                    title = shortTitle,
                    subtitle = desc
                ))
            }
        }

        return events
    }
}
