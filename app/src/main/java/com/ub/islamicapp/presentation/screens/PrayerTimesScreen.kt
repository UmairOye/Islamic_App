package com.ub.islamicapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.presentation.viewmodel.HomeViewModel
import com.ub.islamicapp.theme.LightBackground
import com.ub.islamicapp.theme.PrimaryGreen

@Composable
fun PrayerTimesScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            Text(
                text = uiState.location,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.currentTime,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 72.sp
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.timeRemaining,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(LightBackground)
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Previous Day */ }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Prev")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = uiState.hijriDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = { /* Next Day */ }) {
                        Icon(Icons.Rounded.Notifications, contentDescription = "Next")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(
                    color = Color.LightGray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Prayer", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text("Time", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(uiState.prayerTimes) { prayer ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prayer.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f)
                            )
                            var displayTime = prayer.time
                            if (displayTime != "--:--") {
                                try {
                                    val parts = displayTime.split(":")
                                    val hour24 = parts[0].toInt()
                                    val min = parts[1]
                                    val hour12 = if (hour24 == 0) 12 else if (hour24 > 12) hour24 - 12 else hour24
                                    val amPm = if (hour24 >= 12) "PM" else "AM"
                                    displayTime = String.format("%02d:%s %s", hour12, min, amPm)
                                } catch (e: Exception) {}
                            }

                            Text(
                                text = displayTime,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { /* Toggle Reminder */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen.copy(alpha = 0.1f),
                                    contentColor = PrimaryGreen
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Reminder", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
