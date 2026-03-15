package com.ub.islamicapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.presentation.viewmodel.HomeViewModel
import com.ub.islamicapp.theme.LightBackground
import com.ub.islamicapp.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PrayerTimesScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B) // slate-800
                    )
                }
                Text(
                    text = "Salah Times",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen.copy(alpha = 0.1f))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Notifications, contentDescription = "Notifications", tint = PrimaryGreen)
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen.copy(alpha = 0.1f))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = "Location", tint = PrimaryGreen)
                }
            }
        }

        // Hero Section (Next Prayer)
        HeroSection(
            nextPrayer = uiState.nextPrayer,
            timeRemaining = uiState.timeRemaining,
            hijriDate = uiState.hijriDate,
            location = uiState.location,
            prayerTimes = uiState.prayerTimes
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Schedule Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Schedule",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A) // slate-900
                )
            )
            Text(
                text = "View Calendar",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = PrimaryGreen
                ),
                modifier = Modifier.clickable { /* View Calendar */ }
            )
        }

        // Prayer List
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(uiState.prayerTimes) { prayer ->
                PrayerTimeItem(
                    prayerName = prayer.name,
                    time = prayer.time,
                    isNext = prayer.name == uiState.nextPrayer,
                    isCompleted = prayer.isCompleted
                )
            }
        }
    }
}

@Composable
fun HeroSection(
    nextPrayer: String,
    timeRemaining: String,
    hijriDate: String,
    location: String,
    prayerTimes: List<com.ub.islamicapp.presentation.state.PrayerTime>
) {
    // Determine the target time for the next prayer
    var nextPrayerTimeStr = ""
    var nextPrayerAmPm = ""

    val nextPrayerObj = prayerTimes.find { it.name == nextPrayer }
    if (nextPrayerObj != null && nextPrayerObj.time != "--:--") {
        try {
            val parts = nextPrayerObj.time.split(":")
            val hour24 = parts[0].toInt()
            val min = parts[1]
            val hour12 = if (hour24 == 0) 12 else if (hour24 > 12) hour24 - 12 else hour24
            nextPrayerAmPm = if (hour24 >= 12) "PM" else "AM"
            nextPrayerTimeStr = String.format("%02d:%s", hour12, min)
        } catch (e: Exception) {}
    }

    // Get current gregorian date formatted
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val gregorianDate = dateFormat.format(calendar.time)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        PrimaryGreen,
                        Color(0xFF155D4B) // Darker green shade
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "UP NEXT" Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "UP NEXT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (nextPrayer == "--") "Loading" else nextPrayer,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (nextPrayerTimeStr.isEmpty()) "--:--" else nextPrayerTimeStr,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 64.sp,
                        letterSpacing = (-2).sp,
                        color = Color.White
                    )
                )
                if (nextPrayerAmPm.isNotEmpty()) {
                    Text(
                        text = nextPrayerAmPm,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Starts in ..." Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Rounded.Schedule,
                        contentDescription = "Time remaining",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timeRemaining,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Divider(color = Color.White.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = gregorianDate,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = hijriDate,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = location,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerTimeItem(
    prayerName: String,
    time: String,
    isNext: Boolean,
    isCompleted: Boolean
) {
    var displayTime = time
    var amPm = ""
    if (displayTime != "--:--") {
        try {
            val parts = displayTime.split(":")
            val hour24 = parts[0].toInt()
            val min = parts[1]
            val hour12 = if (hour24 == 0) 12 else if (hour24 > 12) hour24 - 12 else hour24
            amPm = if (hour24 >= 12) "PM" else "AM"
            displayTime = String.format("%02d:%s", hour12, min)
        } catch (e: Exception) {}
    }

    val icon: ImageVector
    val subtitle: String

    when (prayerName) {
        "Fajr" -> { icon = Icons.Rounded.WbTwilight; subtitle = "Dawn Prayer" }
        "Sunrise" -> { icon = Icons.Rounded.WbSunny; subtitle = "No Prayer" }
        "Dhuhr" -> { icon = Icons.Rounded.WbSunny; subtitle = "Noon Prayer" }
        "Asr" -> { icon = Icons.Rounded.WbCloudy; subtitle = "Afternoon Prayer" }
        "Maghrib" -> { icon = Icons.Rounded.WbShade; subtitle = "Sunset Prayer" }
        "Isha" -> { icon = Icons.Rounded.NightsStay; subtitle = "Night Prayer" }
        else -> { icon = Icons.Rounded.Schedule; subtitle = "Prayer" }
    }

    val backgroundColor: Color
    val borderColor: Color
    val iconBoxColor: Color
    val iconColor: Color
    val titleColor: Color
    val timeColor: Color

    if (isNext) {
        backgroundColor = PrimaryGreen.copy(alpha = 0.05f)
        borderColor = PrimaryGreen.copy(alpha = 0.2f)
        iconBoxColor = PrimaryGreen
        iconColor = Color.White
        titleColor = PrimaryGreen
        timeColor = PrimaryGreen
    } else if (prayerName == "Sunrise") {
        backgroundColor = Color(0xFFF1F5F9).copy(alpha = 0.5f) // slate-100
        borderColor = Color.Transparent
        iconBoxColor = Color(0xFFE2E8F0) // slate-200
        iconColor = Color(0xFF64748B) // slate-500
        titleColor = Color(0xFF64748B) // slate-500
        timeColor = Color(0xFF64748B) // slate-500
    } else {
        backgroundColor = Color.White
        borderColor = PrimaryGreen.copy(alpha = 0.05f)
        iconBoxColor = PrimaryGreen.copy(alpha = 0.05f)
        iconColor = PrimaryGreen
        titleColor = Color(0xFF0F172A) // slate-900
        timeColor = Color(0xFF334155) // slate-700
    }

    val contentAlpha = if (prayerName == "Sunrise") 0.7f else 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isNext) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBoxColor.copy(alpha = contentAlpha)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor.copy(alpha = contentAlpha),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = prayerName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = titleColor.copy(alpha = contentAlpha)
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isNext) PrimaryGreen.copy(alpha = 0.7f) else Color(0xFF64748B).copy(alpha = contentAlpha) // slate-500
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(displayTime)
                        }
                        if (amPm.isNotEmpty()) {
                            append(" ")
                            append(amPm)
                        }
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = timeColor.copy(alpha = contentAlpha)
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = if (isNext) Icons.Rounded.NotificationsActive else if (isCompleted) Icons.Rounded.NotificationsOff else Icons.Rounded.Notifications,
                    contentDescription = "Notification",
                    tint = if (isNext) PrimaryGreen else Color(0xFF94A3B8).copy(alpha = contentAlpha), // slate-400
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
