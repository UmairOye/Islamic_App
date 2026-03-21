package com.ub.islamicapp.screens.location.screen

import androidx.compose.ui.res.stringResource
import com.ub.islamicapp.R
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
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.screens.home.viewmodel.HomeViewModel
import com.ub.islamicapp.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf(emptyList<String>()) }
    val coroutineScope = rememberCoroutineScope()
    val scope = rememberCoroutineScope()
    var showPermissionSheet by remember { mutableStateOf(false) }
    var isDetectingLocation by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isDetectingLocation = true
            scope.launch {
                // Trigger actual fetch with slight artificial delay for UX
                viewModel.updateLocationAndPrayers(forceRefresh = true)
                delay(1000)
                isDetectingLocation = false
                navController.popBackStack()
            }
        } else {
            showPermissionSheet = true
            isDetectingLocation = false
        }
    }

    if (showPermissionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPermissionSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.location_permission_required),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.location_permission_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        showPermissionSheet = false
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text(stringResource(id = R.string.grant_permission), color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = TextDark
                )
            }
            Text(
                text = stringResource(id = R.string.select_location),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                .clickable(enabled = !isDetectingLocation) {
                    isDetectingLocation = true
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isDetectingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PrimaryGreen,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Rounded.LocationOn, contentDescription = stringResource(id = R.string.current_location), tint = PrimaryGreen)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isDetectingLocation) stringResource(id = R.string.detecting_location) else stringResource(id = R.string.detect_my_current_location),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                coroutineScope.launch {
                    if (query.length > 2) {
                        suggestions = mockCitySearch(query)
                    } else {
                        suggestions = emptyList()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text(stringResource(id = R.string.search_city)) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = stringResource(id = R.string.search), tint = TextMuted) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = BorderLight,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (suggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { city ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable {
                                scope.launch {
                                    val coords = getCoordinatesFromCity(context, city)
                                    if (coords != null) {
                                        viewModel.saveLocationAndFetchPrayers(coords.first, coords.second, city)
                                    }
                                    navController.popBackStack()
                                }
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary
                        )
                    }
                }
            }
        } else {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.popular_cities),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val popularCities = listOf("Mecca, Saudi Arabia", "Medina, Saudi Arabia", "Istanbul, Turkey", "London, UK", "Rawalpindi, Pakistan")

                popularCities.forEach { city ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .clickable {
                                scope.launch {
                                    val coords = getCoordinatesFromCity(context, city)
                                    if (coords != null) {
                                        viewModel.saveLocationAndFetchPrayers(coords.first, coords.second, city)
                                    }
                                    navController.popBackStack()
                                }
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

private suspend fun mockCitySearch(query: String): List<String> {
    return withContext(Dispatchers.IO) {
        val allCities = listOf(
            "Rawalpindi, Pakistan",
            "Lahore, Pakistan",
            "Karachi, Pakistan",
            "London, UK",
            "Birmingham, UK",
            "New York, USA",
            "Los Angeles, USA",
            "Istanbul, Turkey",
            "Mecca, Saudi Arabia",
            "Medina, Saudi Arabia"
        )
        allCities.filter { it.contains(query, ignoreCase = true) }
    }
}

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
