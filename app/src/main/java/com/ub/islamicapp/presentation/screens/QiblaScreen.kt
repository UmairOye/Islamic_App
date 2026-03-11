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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
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

                // Neumorphic compass design
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .shadow(elevation = 20.dp, shape = CircleShape, spotColor = Color.LightGray.copy(alpha = 0.5f))
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer ticking and rotating cardinal text
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(animatedRotation),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val radius = size.minDimension / 2f
                            val center = Offset(size.width / 2f, size.height / 2f)

                            // Draw ticks
                            for (i in 0 until 360 step 30) {
                                val isCardinal = i % 90 == 0
                                val tickLength = if (isCardinal) 16.dp.toPx() else 8.dp.toPx()
                                val strokeWidth = if (isCardinal) 3.dp.toPx() else 1.5.dp.toPx()
                                val color = if (i == 0) Color.Red else if (isCardinal) PrimaryGreen else Color.LightGray

                                val angleRad = Math.toRadians(i.toDouble() - 90.0)
                                val startRadius = radius - 24.dp.toPx()
                                val endRadius = startRadius - tickLength

                                val start = Offset(
                                    (center.x + startRadius * cos(angleRad)).toFloat(),
                                    (center.y + startRadius * sin(angleRad)).toFloat()
                                )
                                val end = Offset(
                                    (center.x + endRadius * cos(angleRad)).toFloat(),
                                    (center.y + endRadius * sin(angleRad)).toFloat()
                                )

                                drawLine(
                                    color = color,
                                    start = start,
                                    end = end,
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            }
                        }

                        // Cardinal Directions Text
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text("N", modifier = Modifier.align(Alignment.TopCenter).padding(16.dp), color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("E", modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp), color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("S", modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp), color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("W", modifier = Modifier.align(Alignment.CenterStart).padding(16.dp), color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }

                    // Qibla Dot Indicator on the track
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(animatedQiblaRotation),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(x = 56.dp)
                                .size(24.dp)
                                .shadow(8.dp, CircleShape, spotColor = PrimaryGreen)
                                .background(PrimaryGreen, CircleShape)
                        )
                    }

                    // Inner elevated circle with readout
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .shadow(elevation = 16.dp, shape = CircleShape, spotColor = Color.LightGray.copy(alpha = 0.5f))
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val displayAzimuth = (azimuth % 360).toInt()
                            Text(
                                text = "$displayAzimuth°",
                                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, fontSize = 48.sp),
                                color = Color.DarkGray
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