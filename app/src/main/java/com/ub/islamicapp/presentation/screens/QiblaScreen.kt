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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.cos
import kotlin.math.sin
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

    // Maintain a continuous angle to prevent 360 to 0 snap spins
    var continuousAzimuth by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(azimuth) {
        val diff = azimuth - (continuousAzimuth % 360)
        continuousAzimuth += when {
            diff > 180 -> diff - 360
            diff < -180 -> diff + 360
            else -> diff
        }
    }

    val animatedRotation by animateFloatAsState(
        targetValue = -continuousAzimuth,
        animationSpec = tween(durationMillis = 300),
        label = "compass_rotation"
    )

    val animatedQiblaRotation by animateFloatAsState(
        targetValue = uiState.qiblaDirection - continuousAzimuth,
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
                val displayAzimuth = (azimuth % 360).toInt()
                Text(
                    text = "$displayAzimuth Degree",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(280.dp)
                            .shadow(elevation = 4.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        // We will rotate the whole canvas to simulate the compass dial turning.
                        // However, to keep the top text static relative to the dial, we rotate the drawing scope
                        rotate(degrees = animatedRotation) {
                            drawTicks()
                            drawNumbers()
                            drawDirections()
                            // The needle points to the Qibla on the rotating dial
                            rotate(degrees = uiState.qiblaDirection) {
                                drawNeedle()
                            }
                        }
                    }

                    // Center Indicator
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kaaba",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawTicks() {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)

    for (angle in 0 until 360 step 10) {
        val isMajor = angle % 30 == 0
        val lineLength = if (isMajor) 16.dp.toPx() else 8.dp.toPx()
        val strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
        val color = if (isMajor) PrimaryGreen else Color.LightGray

        rotate(degrees = angle.toFloat(), pivot = center) {
            drawLine(
                color = color,
                start = Offset(center.x, center.y - radius + 16.dp.toPx()),
                end = Offset(center.x, center.y - radius + 16.dp.toPx() + lineLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawNumbers() {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)

    val paint = Paint().apply {
        color = android.graphics.Color.parseColor("#1B5E20") // Primary Green approx
        textSize = 12.sp.toPx()
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }

    for (angle in 0 until 360 step 30) {
        // We want the text to be readable (not upside down if possible, but rotating the canvas handles the whole dial)
        // Draw at the top, and rotate the canvas
        rotate(degrees = angle.toFloat(), pivot = center) {
            drawContext.canvas.nativeCanvas.drawText(
                angle.toString(),
                center.x,
                center.y - radius + 12.dp.toPx(),
                paint
            )
        }
    }
}

private fun DrawScope.drawDirections() {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)

    val basePaint = Paint().apply {
        textSize = 16.sp.toPx()
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }

    val directions = listOf(
        Pair("N", android.graphics.Color.RED),
        Pair("E", android.graphics.Color.DKGRAY),
        Pair("S", android.graphics.Color.DKGRAY),
        Pair("W", android.graphics.Color.DKGRAY)
    )

    for ((i, dir) in directions.withIndex()) {
        val angle = i * 90f
        val paint = Paint(basePaint).apply { color = dir.second }
        rotate(degrees = angle, pivot = center) {
            drawContext.canvas.nativeCanvas.drawText(
                dir.first,
                center.x,
                center.y - radius + 48.dp.toPx(), // Further inside
                paint
            )
        }
    }
}

private fun DrawScope.drawNeedle() {
    val center = Offset(size.width / 2f, size.height / 2f)
    val needleLength = 60.dp.toPx()
    val needleWidth = 24.dp.toPx()

    // Top Triangle (Yellow)
    val topPath = Path().apply {
        moveTo(center.x, center.y - needleLength)
        lineTo(center.x + needleWidth / 2f, center.y - needleLength / 2f)
        lineTo(center.x - needleWidth / 2f, center.y - needleLength / 2f)
        close()
    }

    // Bottom Triangle (Blue/Grey)
    val bottomPath = Path().apply {
        moveTo(center.x - needleWidth / 2f, center.y - needleLength / 2f)
        lineTo(center.x + needleWidth / 2f, center.y - needleLength / 2f)
        lineTo(center.x, center.y - 10.dp.toPx())
        close()
    }

    // Light green/teal faint background shadow or wide pointer behind it
    val shadowPath = Path().apply {
        moveTo(center.x, center.y - needleLength - 8.dp.toPx())
        lineTo(center.x + needleWidth, center.y - needleLength / 2f)
        lineTo(center.x, center.y)
        lineTo(center.x - needleWidth, center.y - needleLength / 2f)
        close()
    }

    drawPath(
        path = shadowPath,
        color = PrimaryGreen.copy(alpha = 0.2f)
    )

    drawPath(
        path = topPath,
        color = Color(0xFFFFC107) // Yellow
    )

    drawPath(
        path = bottomPath,
        color = Color(0xFF607D8B) // Blue-Grey
    )
}