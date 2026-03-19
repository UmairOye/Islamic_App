package com.ub.islamicapp.screens.home.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.R
import com.ub.islamicapp.screens.home.components.*
import com.ub.islamicapp.screens.home.viewmodel.HomeViewModel
import com.ub.islamicapp.theme.LightBackground
import com.ub.islamicapp.theme.PrimaryGreen

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isCoarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val isFineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (isCoarseGranted || isFineGranted) {
            viewModel.updateLocationAndPrayers()
        }
    }

    LaunchedEffect(Unit) {
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasCoarse || hasFine) {
            viewModel.updateLocationAndPrayers()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        containerColor = PrimaryGreen,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {

                val hijriStr = uiState.hijriDate?.let { date ->
                    if (date.fallbackString != null) {
                        date.fallbackString
                    } else {
                        val monthName = when (date.monthIndex) {
                            0 -> stringResource(R.string.month_muharram)
                            1 -> stringResource(R.string.month_safar)
                            2 -> stringResource(R.string.month_rabi_al_awwal)
                            3 -> stringResource(R.string.month_rabi_al_thani)
                            4 -> stringResource(R.string.month_jumada_al_awwal)
                            5 -> stringResource(R.string.month_jumada_al_thani)
                            6 -> stringResource(R.string.month_rajab)
                            7 -> stringResource(R.string.month_shaban)
                            8 -> stringResource(R.string.month_ramadan)
                            9 -> stringResource(R.string.month_shawwal)
                            10 -> stringResource(R.string.month_dhu_al_qidah)
                            11 -> stringResource(R.string.month_dhu_al_hijjah)
                            else -> ""
                        }
                        stringResource(R.string.hijri_date_format, date.day, monthName, date.year)
                    }
                } ?: stringResource(R.string.unknown_date)

                val timeRemStr = uiState.timeRemaining?.let { tr ->
                    val pName = when (tr.prayerName) {
                        "Fajr" -> stringResource(R.string.prayer_fajr)
                        "Sunrise" -> stringResource(R.string.prayer_sunrise)
                        "Dhuhr" -> stringResource(R.string.prayer_dhuhr)
                        "Asr" -> stringResource(R.string.prayer_asr)
                        "Maghrib" -> stringResource(R.string.prayer_maghrib)
                        "Isha" -> stringResource(R.string.prayer_isha)
                        else -> tr.prayerName
                    }
                    if (tr.hours > 0) {
                        stringResource(R.string.time_left_format, pName, tr.hours, tr.minutes)
                    } else {
                        stringResource(R.string.time_left_no_hours_format, pName, tr.minutes)
                    }
                } ?: "--"

                val locStr = uiState.location.ifEmpty { stringResource(R.string.unknown_location) }

                HomeTopHeader(
                    hijriDate = hijriStr,
                    location = locStr,
                    currentTime = uiState.currentTime,
                    timeRemaining = timeRemStr,
                    prayers = uiState.prayerTimes.filter { it.name != "Sunrise" },
                    nextPrayer = uiState.nextPrayer,
                    isLocationError = uiState.error == "NO_LOCATION" || uiState.error != null
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(LightBackground)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {

                    FeatureGrid(
                        onNavigateToHijri = { navController.navigate("hijri_calendar") },
                        onNavigateToCalendar = { navController.navigate("gregorian_calendar") },
                        onNavigateToSalah = { navController.navigate("prayer_times") },
                        onNavigateToQibla = { navController.navigate("qibla") }
                    )

                    LastReadCard()

                    if (uiState.error != "NO_LOCATION" && uiState.prayerTimes.isNotEmpty()) {
                        PrayerTracker(prayers = uiState.prayerTimes.filter { it.name != "Sunrise" })
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
