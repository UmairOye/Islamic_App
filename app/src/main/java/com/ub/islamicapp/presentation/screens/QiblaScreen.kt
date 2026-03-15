package com.ub.islamicapp.presentation.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import com.ub.islamicapp.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ub.islamicapp.presentation.viewmodel.QiblaViewModel
import com.ub.islamicapp.theme.PrimaryGreen
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    navController: NavController,
    viewModel: QiblaViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var azimuth by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    val magnetometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    var continuousAzimuth by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {

        val sensorListener = object : SensorEventListener {

            var gravity = FloatArray(3)
            var geomagnetic = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent) {

                when (event.sensor.type) {

                    Sensor.TYPE_ACCELEROMETER ->
                        gravity = event.values.clone()

                    Sensor.TYPE_MAGNETIC_FIELD ->
                        geomagnetic = event.values.clone()
                }

                val R = FloatArray(9)
                val I = FloatArray(9)

                val success = SensorManager.getRotationMatrix(
                    R,
                    I,
                    gravity,
                    geomagnetic
                )

                if (success) {

                    val orientation = FloatArray(3)

                    SensorManager.getOrientation(R, orientation)

                    val azimuthRadians = orientation[0]

                    val azimuthDegrees =
                        (Math.toDegrees(azimuthRadians.toDouble()) + 360).toFloat() % 360

                    azimuth = azimuthDegrees
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(
            sensorListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )

        sensorManager.registerListener(
            sensorListener,
            magnetometer,
            SensorManager.SENSOR_DELAY_UI
        )

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

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
        animationSpec = tween(300),
        label = "compass_rotation"
    )

    val animatedQiblaRotation by animateFloatAsState(
        targetValue = uiState.qiblaDirection - continuousAzimuth,
        animationSpec = tween(300),
        label = "qibla_rotation"
    )

    val direction = remember(uiState.qiblaDirection) {
        when (uiState.qiblaDirection) {
            in 0f..22f, in 338f..360f -> "N"
            in 23f..67f -> "NE"
            in 68f..112f -> "E"
            in 113f..157f -> "SE"
            in 158f..202f -> "S"
            in 203f..247f -> "SW"
            in 248f..292f -> "W"
            else -> "NW"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Qibla") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {

                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            rotationZ = animatedRotation
                        }
                ) {
                    drawCompassDial()
                }

                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    drawNeedle()
                }

                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    rotate(animatedQiblaRotation) {
                        drawQiblaArrow()
                    }
                }

                Icon(
                    painter = painterResource(R.drawable.kaaba),
                    contentDescription = "Kaaba",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 40.dp)
                        .size(42.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "${uiState.qiblaDirection.toInt()}° $direction",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Your Location: ",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}


private fun DrawScope.drawCompassDial() {

    val radius = size.minDimension / 2
    val center = Offset(size.width / 2, size.height / 2)

    drawCircle(
        color = Color.White,
        radius = radius
    )

    for (angle in 0 until 360 step 10) {

        val lineLength =
            if (angle % 30 == 0) 25f else 12f

        rotate(angle.toFloat(), center) {

            drawLine(
                color = Color.LightGray,
                start = Offset(center.x, center.y - radius + 10f),
                end = Offset(center.x, center.y - radius + lineLength),
                strokeWidth = 3f
            )
        }
    }
}


private fun DrawScope.drawNeedle() {

    val center = Offset(size.width / 2, size.height / 2)

    val north = Path().apply {
        moveTo(center.x, center.y - 110f)
        lineTo(center.x + 20f, center.y)
        lineTo(center.x - 20f, center.y)
        close()
    }

    val south = Path().apply {
        moveTo(center.x, center.y + 110f)
        lineTo(center.x + 20f, center.y)
        lineTo(center.x - 20f, center.y)
        close()
    }

    drawPath(north, Color.Blue)
    drawPath(south, Color.Green)

    drawCircle(
        color = Color.Gray,
        radius = 12f,
        center = center
    )
}


private fun DrawScope.drawQiblaArrow() {

    val center = Offset(size.width / 2, size.height / 2)

    drawLine(
        color = Color.Black,
        start = Offset(center.x, center.y),
        end = Offset(size.width - 40f, center.y),
        strokeWidth = 6f
    )
}