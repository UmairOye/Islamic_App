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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ub.islamicapp.presentation.components.*
import com.ub.islamicapp.presentation.viewmodel.HomeViewModel
import com.ub.islamicapp.theme.PrimaryGreen
import com.ub.islamicapp.theme.LightBackground

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
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
        containerColor = PrimaryGreen, // The base color is green for the header
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            // Background Header Section with Parallax Effect
            HomeTopHeader(
                hijriDate = uiState.hijriDate,
                location = uiState.location,
                currentTime = uiState.currentTime,
                timeRemaining = uiState.timeRemaining,
                prayers = uiState.prayerTimes,
                nextPrayer = uiState.nextPrayer,
                modifier = Modifier
                    .onSizeChanged { headerHeightPx = it.height }
                    .graphicsLayer {
                        // Premium Parallax Effect
                        // Translate Header downwards slightly relative to scroll, giving a parallax depth effect
                        translationY = -scrollState.value * 0.2f
                        
                        // Fade out the header slightly as it goes up
                        alpha = 1f - (scrollState.value.toFloat() / (headerHeightPx * 1.5f)).coerceIn(0f, 1f)
                        
                        // Scale down very slightly
                        val scale = 1f - (scrollState.value.toFloat() / 3000f).coerceIn(0f, 0.05f)
                        scaleX = scale
                        scaleY = scale
                    }
            )
            
            // Scrollable Content overlaying the header
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Spacer matching the header's true height so content begins right below it
                val headerHeightDp = with(density) { headerHeightPx.toDp() }
                Spacer(modifier = Modifier.height(headerHeightDp))
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 24.dp, 
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                            spotColor = androidx.compose.ui.graphics.Color.Black,
                            ambientColor = androidx.compose.ui.graphics.Color.Black
                        )
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(LightBackground)
                        .padding(vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    FeatureGrid()
                    
                    LastReadCard(
                        surah = uiState.lastReadSurah,
                        verse = uiState.lastReadVerse,
                        onContinueClick = { /* Handle click */ }
                    )
                    
                    PrayerTracker(prayers = uiState.prayerTimes)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
