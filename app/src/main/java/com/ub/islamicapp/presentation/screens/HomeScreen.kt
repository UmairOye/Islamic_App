package com.ub.islamicapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.presentation.components.*
import com.ub.islamicapp.presentation.viewmodel.HomeViewModel
import com.ub.islamicapp.theme.PrimaryGreen
import com.ub.islamicapp.theme.LightBackground

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
        val granted = permissions.values.all { it }
        if (granted) {
            viewModel.updateLocationAndPrayers()
        }
    }

    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) {
            viewModel.updateLocationAndPrayers()
        } else {
            permissionLauncher.launch(permissions)
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
//                val headerHeightDp = with(density) { headerHeightPx.toDp() }
//                Spacer(modifier = Modifier.height(headerHeightDp))


                HomeTopHeader(
                    hijriDate = uiState.hijriDate,
                    location = uiState.location,
                    currentTime = uiState.currentTime,
                    timeRemaining = uiState.timeRemaining,
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

                    LastReadCard(
                      
                    )

                    if (uiState.error != "NO_LOCATION" && uiState.prayerTimes.isNotEmpty()) {
                        PrayerTracker(prayers = uiState.prayerTimes.filter { it.name != "Sunrise" })
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
