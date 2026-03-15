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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.R
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

                        if (Math.abs(azimuth - azimuthInDegrees) > 1.0f && Math.abs(azimuth - azimuthInDegrees) < 359.0f) {
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

    var continuousAzimuth by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(azimuth) {
        val diff = azimuth - (continuousAzimuth % 360)
        continuousAzimuth += when {
            diff > 180 -> diff - 360
            diff < -180 -> diff + 360
            else -> diff
        }
    }

    val animatedQiblaRotation by animateFloatAsState(
        targetValue = uiState.qiblaDirection - continuousAzimuth,
        animationSpec = tween(durationMillis = 300),
        label = "qibla_rotation"
    )

    Scaffold(
        containerColor = LightBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B)
                    )
                }
                Text(
                    text = "Qibla Direction",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Main Content: Qibla Compass
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
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
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                         CircularProgressIndicator(color = PrimaryGreen)
                         Spacer(modifier = Modifier.height(16.dp))
                         Text(
                            text = "Calculating Qibla direction...",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                     }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer Decorative Ring
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(2.dp, PrimaryGreen.copy(alpha = 0.1f), CircleShape)
                            )

                            // Tick Marks Ring (simulated with Canvas)
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize(0.9f)
                                    .clip(CircleShape)
                            ) {
                                drawCompassTicks()
                            }

                            // Inner Compass Circle
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(0.72f)
                                    .shadow(elevation = 16.dp, shape = CircleShape, spotColor = PrimaryGreen.copy(alpha = 0.3f))
                                    .background(Color.White, CircleShape)
                                    .border(1.dp, Color(0xFFF1F5F9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                // Direction Arrow rotating towards Qibla
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .rotate(animatedQiblaRotation),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(top = 16.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = android.R.drawable.arrow_up_float), // Placeholder until we load expand_less
                                            contentDescription = "Direction Arrow",
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(96.dp)
                                                .background(
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            PrimaryGreen.copy(alpha = 0.4f),
                                                            PrimaryGreen
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(50)
                                                )
                                        )
                                    }
                                }

                                // Degree Indicators (Fixed relative to the phone)
                                Text("N", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF94A3B8), modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp))
                                Text("S", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF94A3B8), modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))
                                Text("W", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF94A3B8), modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp))
                                Text("E", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF94A3B8), modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp))

                                // Center Kaaba Icon
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = PrimaryGreen.copy(alpha = 0.3f))
                                        .background(PrimaryGreen, RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_mosque_silhouette),
                                        contentDescription = "Kaaba",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        val displayAzimuth = (azimuth % 360).toInt()
                        val direction = when (displayAzimuth) {
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
                            text = "Turn your phone to align",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "$displayAzimuth° $direction",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryGreen,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Bottom Prayer Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0xFFF1F5F9))
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(PrimaryGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Decide icon based on prayer name
                                val iconRes = when (uiState.nextPrayerName) {
                                    "Isha" -> android.R.drawable.ic_secure // Dark mode placeholder
                                    "Fajr", "Maghrib" -> android.R.drawable.ic_menu_camera // twilight placeholder
                                    else -> android.R.drawable.ic_dialog_info // sunny placeholder
                                }
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = "Prayer Icon",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "${uiState.nextPrayerName} Prayer",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = uiState.timeRemaining,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = PrimaryGreen,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        var displayTime = uiState.nextPrayerTime
                        if (displayTime != "--:--") {
                            try {
                                val parts = displayTime.split(":")
                                val hour24 = parts[0].toInt()
                                val min = parts[1]
                                val hour12 = if (hour24 == 0) 12 else if (hour24 > 12) hour24 - 12 else hour24
                                val amPm = if (hour24 >= 12) "PM" else "AM"
                                displayTime = String.format("%02d:%s %s", hour12, min, amPm)
                            } catch (e: Exception) {}
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = displayTime,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF0F172A)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                                    contentDescription = "Location",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LOCAL TIME",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Location Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE2E8F0)))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(50))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                            contentDescription = "Near Me",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.locationName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF475569)
                        )
                    }
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE2E8F0)))
                }
            }
        }
    }
}

private fun DrawScope.drawCompassTicks() {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = size.minDimension / 2f

    for (angle in 0 until 360 step 6) {
        val color = if (angle % 30 == 0) PrimaryGreen.copy(alpha = 0.4f) else PrimaryGreen.copy(alpha = 0.1f)
        val strokeWidth = if (angle % 30 == 0) 3.dp.toPx() else 1.5.dp.toPx()
        val length = if (angle % 30 == 0) 12.dp.toPx() else 8.dp.toPx()

        rotate(degrees = angle.toFloat(), pivot = center) {
            drawLine(
                color = color,
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y - radius + length),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
