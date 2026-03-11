package com.ub.islamicapp.presentation.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ub.islamicapp.domain.location.LocationTracker
import com.ub.islamicapp.presentation.state.QiblaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private val QIBLA_LATITUDE = 21.422487
    private val QIBLA_LONGITUDE = 39.826206

    fun onPermissionsResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasLocationPermission = granted)
        if (granted) {
            calculateQiblaDirection()
        }
    }

    fun onSensorsAvailable(available: Boolean) {
        _uiState.value = _uiState.value.copy(hasSensors = available)
    }

    private fun calculateQiblaDirection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingLocation = true)
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                val myLat = Math.toRadians(location.latitude)
                val myLng = Math.toRadians(location.longitude)
                val qiblaLat = Math.toRadians(QIBLA_LATITUDE)
                val qiblaLng = Math.toRadians(QIBLA_LONGITUDE)

                val dLng = qiblaLng - myLng
                val y = sin(dLng) * cos(qiblaLat)
                val x = cos(myLat) * sin(qiblaLat) - sin(myLat) * cos(qiblaLat) * cos(dLng)
                var qibla = Math.toDegrees(atan2(y, x)).toFloat()
                if (qibla < 0) {
                    qibla += 360f
                }
                _uiState.value = _uiState.value.copy(
                    qiblaDirection = qibla,
                    isLoadingLocation = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoadingLocation = false)
            }
        }
    }
}