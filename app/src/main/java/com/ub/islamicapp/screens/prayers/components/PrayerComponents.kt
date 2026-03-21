package com.ub.islamicapp.screens.prayers.components

import com.ub.islamicapp.R
import androidx.compose.ui.res.stringResource
import android.R.attr.shadowColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.screens.home.viewmodel.HomeViewModel
import com.ub.islamicapp.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HeroSection(
    nextPrayer: String,
    timeRemaining: String,
    hijriDate: String,
    location: String,
    prayerTimes: List<com.ub.islamicapp.screens.home.viewmodel.PrayerTime>
) {

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
            nextPrayerTimeStr = String.format(Locale.US,"%02d:%s", hour12, min)
        } catch (e: Exception) {}
    }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val gregorianDate = dateFormat.format(calendar.time)

    Box(
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))

    ) {
        Image(
            painter = painterResource(id = R.drawable.salah_top_header_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.up_next),
                    fontSize = 12.sp,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (nextPrayer == "--") stringResource(id = R.string.loading) else nextPrayer,

                fontSize = 30.sp,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (nextPrayerTimeStr.isEmpty()) "--:--" else nextPrayerTimeStr,
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    color = Color.White,
                    letterSpacing = (-2).sp
                )
                if (nextPrayerAmPm.isNotEmpty()) {
                    Text(
                        text = nextPrayerAmPm,
                        fontSize = 24.sp,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                        contentDescription = stringResource(id = R.string.time_remaining),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timeRemaining,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        fontFamily = InterFontFamily,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            HorizontalDivider( color = Color.White.copy(alpha = 0.1f))

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

    val icon: Int
    val subtitle: String

    when (prayerName) {
        "Fajr" -> { icon = R.drawable.fajar_salah; subtitle = "Dawn Prayer" }
        "Sunrise" -> { icon = R.drawable.sunrise_; subtitle = "No Prayer" }
        "Dhuhr" -> { icon = R.drawable.dhuhr_salah; subtitle = "Noon Prayer" }
        "Asr" -> { icon = R.drawable.asar_salah; subtitle = "Afternoon Prayer" }
        "Maghrib" -> { icon = R.drawable.maghrib_salah; subtitle = "Sunset Prayer" }
        "Isha" -> { icon = R.drawable.isha_salah; subtitle = "Night Prayer" }
        else -> { icon = R.drawable.dhuhr_salah; subtitle = "Prayer" }
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
        backgroundColor = BackgroundLight.copy(alpha = 0.5f)
        borderColor = Color.Transparent
        iconBoxColor = BorderLight
        iconColor = TextSecondary
        titleColor = TextSecondary
        timeColor = TextSecondary
    } else {
        backgroundColor = Color.White
        borderColor = PrimaryGreen.copy(alpha = 0.05f)
        iconBoxColor = PrimaryGreen.copy(alpha = 0.05f)
        iconColor = PrimaryGreen
        titleColor = TextPrimary
        timeColor = TextPrimary.copy(alpha = 0.8f)
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
                        .background(if(isNext) PrimaryGreen else PrimaryGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = if(isNext) Color.White else PrimaryGreen ),
                        modifier = Modifier.size(18.dp)
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
                            color = if (isNext) PrimaryGreen.copy(alpha = 0.7f) else TextSecondary.copy(alpha = contentAlpha)
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
                    tint = if (isNext) PrimaryGreen else TextMuted.copy(alpha = contentAlpha),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
