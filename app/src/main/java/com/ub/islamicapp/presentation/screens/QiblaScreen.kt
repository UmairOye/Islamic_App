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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ub.islamicapp.R
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

            // Low-pass filter constant (0.0 - 1.0)
            // Smaller value = smoother but slower to update
            val alpha = 0.05f

            private fun lowPassFilter(input: FloatArray, output: FloatArray?): FloatArray {
                if (output == null) return input
                for (i in input.indices) {
                    output[i] = output[i] + alpha * (input[i] - output[i])
                }
                return output
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor === accelerometer) {
                    lastAccelerometer = lowPassFilter(event.values.clone(), lastAccelerometer)
                    lastAccelerometerSet = true
                } else if (event.sensor === magnetometer) {
                    lastMagnetometer = lowPassFilter(event.values.clone(), lastMagnetometer)
                    lastMagnetometerSet = true
                }

                if (lastAccelerometerSet && lastMagnetometerSet) {
                    val r = FloatArray(9)
                    if (SensorManager.getRotationMatrix(r, null, lastAccelerometer, lastMagnetometer)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)
                        val azimuthInRadians = orientation[0]

                        var azimuthInDegrees = (Math.toDegrees(azimuthInRadians.toDouble()) + 360).toFloat() % 360

                        // Additional smoothing specifically for the final degree output to avoid jitter
                        // Using a simple threshold check (if change is very small, ignore it)
                        if (Math.abs(azimuth - azimuthInDegrees) > 1.0f && Math.abs(azimuth - azimuthInDegrees) < 359.0f) {
                            // Circular smoothing for the final angle
                            val diff = azimuthInDegrees - azimuth
                            if (diff > 180) azimuthInDegrees -= 360
                            else if (diff < -180) azimuthInDegrees += 360

                            azimuth = (azimuth + 0.1f * (azimuthInDegrees - azimuth))
                            if (azimuth < 0) azimuth += 360f
                            if (azimuth >= 360f) azimuth -= 360f
                        }
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

                // Compass design using provided assets
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer static ring (Shadow/Background)
                    Image(
                        painter = painterResource(id = R.drawable.qibla_outer_ring_2),
                        contentDescription = "Compass Background",
                        modifier = Modifier.fillMaxSize(0.9f)
                    )

                    // Outer ticking ring (Rotating)
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.85f)
                            .rotate(animatedRotation),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qibla_outer_ring_1),
                            contentDescription = "Compass Outer Ring",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Qibla Dot Indicator on the track (Rotating relative to Qibla)
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.85f)
                            .rotate(animatedQiblaRotation),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qibla_reading),
                            contentDescription = "Qibla Indicator",
                            modifier = Modifier
                                .padding(top = 56.dp)
                                .size(24.dp)
                        )
                    }

                    // Inner elevated circle with readout
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.5f)
                            .clip(CircleShape)
                            .background(Color.White)
                            .shadow(elevation = 16.dp, shape = CircleShape, spotColor = Color.LightGray.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            val displayAzimuth = (azimuth % 360).toInt()
                            Text(
                                text = "$displayAzimuth°",
                                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, fontSize = 42.sp),
                                color = Color(0xFF4A5568)
                            )
                            val cardinal = when (displayAzimuth) {
                                in 338..359, in 0..22 -> "N"
                                in 23..67 -> "NE"
                                in 68..112 -> "E"
                                in 113..157 -> "SE"
                                in 158..202 -> "S"
                                in 203..247 -> "SW"
                                in 248..292 -> "W"
                                in 293..337 -> "NW"
                                else -> ""
                            }
                            Text(
                                text = cardinal,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}