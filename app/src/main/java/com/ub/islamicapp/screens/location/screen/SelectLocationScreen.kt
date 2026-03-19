package com.ub.islamicapp.screens.location.screen

import android.Manifest
import android.content.Context
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.ub.islamicapp.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.screens.home.viewmodel.HomeViewModel
import com.ub.islamicapp.screens.location.components.CityRow
import com.ub.islamicapp.screens.location.model.CityData
import com.ub.islamicapp.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocationScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasCoarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val hasFine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (hasCoarse || hasFine) {
            viewModel.updateLocationAndPrayers(forceRefresh = true)
            navController.popBackStack()
        }
    }

    val popularCities = listOf(
        CityData("Cairo, Egypt", "UTC +2:00", Icons.Rounded.Star, ThemeLightGreenBg, ThemeDarkGreenIcon),
        CityData("Istanbul, Turkey", "UTC +3:00", Icons.Rounded.Star, ThemeLightGreenBg, ThemeDarkGreenIcon),
        CityData("Jakarta, Indonesia", "UTC +7:00", Icons.Rounded.Star, ThemeLightGreenBg, ThemeDarkGreenIcon),
        CityData("London, United Kingdom", "UTC +0:00", Icons.Rounded.Star, ThemeLightGreenBg, ThemeDarkGreenIcon)
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.title_select_location),
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
                Spacer(modifier = Modifier.height(16.dp))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Detect My Location
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ThemeLightGreenBg)
                        .border(1.dp, ThemeDarkGreenIcon.copy(alpha=0.3f), RoundedCornerShape(12.dp))
                        .clickable {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.MyLocation, contentDescription = "Detect", tint = ThemeDarkGreenIcon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.detect_my_location),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ThemeDarkGreenIcon
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState.recentCities.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.recent_cities),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = ThemeGrayText
                        ),
                        modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp)
                    )
                }
            }

            if (uiState.recentCities.isNotEmpty()) {
                items(uiState.recentCities) { cityEntity ->
                    CityRow(
                        city = CityData(cityEntity.cityName, "Recently used", Icons.Rounded.History, ThemeButtonBg, ThemeGrayText),
                        onClick = {
                            viewModel.saveLocationAndFetchPrayers(cityEntity.latitude, cityEntity.longitude, cityEntity.cityName)
                            navController.popBackStack()
                        }
                    )
                    HorizontalDivider(color = ThemeDivider, thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.popular_cities),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = ThemeGrayText
                        ),
                        modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp)
                    )
                }

                items(popularCities) { city ->
                    CityRow(
                        city = city,
                        onClick = {
                            scope.launch {
                                val coords = getCoordinatesFromCity(context, city.name)
                                if (coords != null) {
                                    viewModel.saveLocationAndFetchPrayers(coords.first, coords.second, city.name)
                                }
                                navController.popBackStack()
                            }
                        }
                    )
                    HorizontalDivider(color = ThemeDivider, thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = stringResource(R.string.location_usage_description),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF94A3B8)
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
                    )
                }
        }
    }
}

// Keep the existing helper logic for geocoding cleanly below
private suspend fun getCoordinatesFromCity(context: Context, cityName: String): Pair<Double, Double>? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(cityName, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Pair(address.latitude, address.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
