package com.ub.islamicapp.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ub.islamicapp.screens.calendar.viewmodel.CalendarViewModel

@Composable
fun CalendarView(
    isHijri: Boolean,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(isHijri) {
        viewModel.init(isHijri)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            Column {
                CalendarHeader(
                    title = uiState.monthData.monthName,
                    subtitle = "${uiState.monthData.year}",
                    onPreviousMonth = { viewModel.changeMonth(-1) },
                    onNextMonth = { viewModel.changeMonth(1) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CalendarGrid(
                    days = uiState.monthData.days,
                    selectedDay = uiState.selectedDay,
                    onDaySelected = { viewModel.selectDay(it) }
                )
            }
        }

        if (uiState.upcomingEvents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            UpcomingEventsList(events = uiState.upcomingEvents)
        }
    }
}
