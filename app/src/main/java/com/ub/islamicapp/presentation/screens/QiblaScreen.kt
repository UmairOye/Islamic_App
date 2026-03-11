package com.ub.islamicapp.presentation.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.presentation.viewmodel.QiblaViewModel
import com.ub.islamicapp.theme.LightBackground
import com.ub.islamicapp.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    navController: NavController,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var azimuth by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    val magnetometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) }

    LaunchedEffect(Unit) {
        viewModel.onSensorsAvailable(accelerometer != null && magnetometer != null)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        viewModel.onPermissionsResult(granted)
    }

    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        viewModel.onPermissionsResult(allGranted)
        if (!allGranted) {
            permissionLauncher.launch(permissions)
        }
    }

    // Sensor listener
    DisposableEffect(uiState.hasSensors) {
        val sensorEventListener = object : SensorEventListener {
            var lastAccelerometer = FloatArray(3)
            var lastMagnetometer = FloatArray(3)
            var lastAccelerometerSet = false
            var lastMagnetometerSet = false

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor === accelerometer) {
                    System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                    lastAccelerometerSet = true
                } else if (event.sensor === magnetometer) {
                    System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                    lastMagnetometerSet = true
                }

                if (lastAccelerometerSet && lastMagnetometerSet) {
                    val r = FloatArray(9)
                    if (SensorManager.getRotationMatrix(r, null, lastAccelerometer, lastMagnetometer)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)
                        val azimuthInRadians = orientation[0]
                        val azimuthInDegrees = (Math.toDegrees(azimuthInRadians.toDouble()) + 360).toFloat() % 360
                        azimuth = azimuthInDegrees
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (uiState.hasSensors) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            if (uiState.hasSensors) {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }
    }

    val animatedRotation by animateFloatAsState(
        targetValue = -azimuth,
        animationSpec = tween(durationMillis = 300),
        label = "compass_rotation"
    )

    val animatedQiblaRotation by animateFloatAsState(
        targetValue = uiState.qiblaDirection - azimuth,
        animationSpec = tween(durationMillis = 300),
        label = "qibla_rotation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Qibla Direction") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBackground)
            )
        },
        containerColor = LightBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!uiState.hasSensors) {
                Text(
                    text = "Your device doesn't support compass sensors required for Qibla direction.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            } else if (!uiState.hasLocationPermission) {
                 Text(
                    text = "Location permission is required to calculate accurate Qibla direction.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            } else if (uiState.isLoadingLocation) {
                 CircularProgressIndicator(color = PrimaryGreen)
                 Spacer(modifier = Modifier.height(16.dp))
                 Text(
                    text = "Calculating Qibla direction...",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Point your phone directly ahead",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Compass dial (North indicator)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(animatedRotation),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "N",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryGreen,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // Qibla Pointer
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(animatedQiblaRotation),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 40.dp)
                                .size(20.dp, 80.dp)
                                .background(PrimaryGreen, shape = CircleShape)
                        )
                    }

                    // Center dot
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "${uiState.qiblaDirection.toInt()}°",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryGreen
                )
                Text(
                    text = "From North",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}