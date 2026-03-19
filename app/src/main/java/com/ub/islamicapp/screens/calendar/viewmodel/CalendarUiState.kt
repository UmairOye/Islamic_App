package com.ub.islamicapp.screens.calendar.viewmodel

import com.ub.islamicapp.utils.MonthData

data class UpcomingEvent(
    val day: String,
    val month: String,
    val title: String,
    val subtitle: String
)

data class CalendarUiState(
    val isHijri: Boolean = true,
    val currentMonthOffset: Int = 0,
    val monthData: MonthData = MonthData("", 0, 0, emptyList()),
    val selectedDay: Int = -1,
    val upcomingEvents: List<UpcomingEvent> = emptyList()
)
