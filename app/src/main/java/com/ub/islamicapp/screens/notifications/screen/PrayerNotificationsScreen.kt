package com.ub.islamicapp.screens.notifications.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ub.islamicapp.R
import com.ub.islamicapp.screens.notifications.components.IconBox
import com.ub.islamicapp.screens.notifications.components.VectorIconBox
import com.ub.islamicapp.screens.notifications.components.PrayerNotificationItem
import com.ub.islamicapp.screens.notifications.components.SectionHeader
import com.ub.islamicapp.screens.notifications.components.SettingsActionItem
import com.ub.islamicapp.screens.notifications.model.PrayerUiData
import com.ub.islamicapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerNotificationsScreen(navController: NavController) {
    var globalNotificationsEnabled by remember { mutableStateOf(true) }

    val prayers = listOf(
        PrayerUiData(stringResource(R.string.prayer_fajr), stringResource(R.string.subtitle_fajr), R.drawable.fajar_salah, ThemeLightGreenBg, ThemeDarkGreenIcon),
        PrayerUiData(stringResource(R.string.prayer_sunrise), stringResource(R.string.subtitle_sunrise), R.drawable.sunrise_, ThemeLightOrangeBg, ThemeDarkOrangeIcon, initialOption = "None"),
        PrayerUiData(stringResource(R.string.prayer_dhuhr), stringResource(R.string.subtitle_dhuhr), R.drawable.dhuhr_salah, ThemeLightGreenBg, ThemeDarkGreenIcon),
        PrayerUiData(stringResource(R.string.prayer_asr), stringResource(R.string.subtitle_asr), R.drawable.asar_salah, ThemeLightGreenBg, ThemeDarkGreenIcon),
        PrayerUiData(stringResource(R.string.prayer_maghrib), stringResource(R.string.subtitle_maghrib), R.drawable.maghrib_salah, ThemeLightGreenBg, ThemeDarkGreenIcon),
        PrayerUiData(stringResource(R.string.prayer_isha), stringResource(R.string.subtitle_isha), R.drawable.isha_salah, ThemeLightGreenBg, ThemeDarkGreenIcon)
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.nav_prayer_notifications),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontSize = 20.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                HorizontalDivider(color = ThemeDivider, thickness = 1.dp)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(stringResource(R.string.general_settings))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        VectorIconBox(
                            icon = Icons.Rounded.NotificationsActive,
                            bgColor = ThemeLightGreenBg,
                            iconColor = ThemeDarkGreenIcon
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.enable_all_notifications),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = stringResource(R.string.global_toggle_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = ThemeGrayText
                            )
                        }
                    }
                    Switch(
                        checked = globalNotificationsEnabled,
                        onCheckedChange = { globalNotificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ThemeDarkGreenIcon,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1),
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                SectionHeader(stringResource(R.string.prayer_reminders))
                Spacer(modifier = Modifier.height(8.dp))
            }

            itemsIndexed(prayers) { index, prayer ->
                PrayerNotificationItem(prayer)
                if (index < prayers.size - 1) {
                    HorizontalDivider(color = ThemeDivider, thickness = 1.dp, modifier = Modifier.padding(start = 80.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = ThemeDivider, thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                SettingsActionItem(title = stringResource(R.string.adhan_audio_selection), icon = Icons.Rounded.VolumeUp)
                Spacer(modifier = Modifier.height(12.dp))
                SettingsActionItem(title = stringResource(R.string.pre_prayer_reminders), icon = Icons.Rounded.Timer)

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = stringResource(R.string.settings_saved_auto),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF94A3B8)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
                )
            }
        }
    }
}
