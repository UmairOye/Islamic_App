package com.ub.islamicapp.screens.calendar.viewmodel

import com.ub.islamicapp.utils.MonthData

import com.ub.islamicapp.screens.calendar.model.UpcomingEvent

data class CalendarUiState(
    val isHijri: Boolean = true,
    val currentMonthOffset: Int = 0,
    val monthData: MonthData = MonthData("", 0, 0, emptyList()),
    val selectedDay: Int = -1,
    val upcomingEvents: List<UpcomingEvent> = emptyList()
)
