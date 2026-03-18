package com.ub.islamicapp.presentation.screens

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
import com.ub.islamicapp.presentation.viewmodel.HomeViewModel
import com.ub.islamicapp.theme.LightBackground
import com.ub.islamicapp.theme.PrimaryGreen
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
    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf(emptyList<String>()) }
    val coroutineScope = rememberCoroutineScope()
    val scope = rememberCoroutineScope()
    var showPermissionSheet by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.updateLocationAndPrayers()
            navController.popBackStack()
        } else {
            showPermissionSheet = true
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
                    text = "Location Permission Required",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We need your location to calculate accurate prayer times and Qibla direction based on astronomical formulas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
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
                    Text("Grant Permission", color = Color.White)
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
                    contentDescription = "Back",
                    tint = Color(0xFF1E293B)
                )
            }
            Text(
                text = "Select Location",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .clickable {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocationOn, contentDescription = "Current Location", tint = PrimaryGreen)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Detect My Current Location",
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
            placeholder = { Text("Search city...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search", tint = Color(0xFF94A3B8)) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Color(0xFFE2E8F0),
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
                                // TODO: Properly handle setting coordinates with Geocoder or Room
                                // Mocking the selection update for now since Geocoder is not robust in all sandboxes
                                navController.popBackStack()
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }
        } else {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Popular Cities",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF64748B),
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
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .clickable {
                                // TODO: Properly handle setting coordinates with Geocoder or Room
                                navController.popBackStack()
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF0F172A)
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
