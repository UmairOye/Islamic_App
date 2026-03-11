package com.ub.islamicapp.presentation.state

data class QiblaUiState(
    val qiblaDirection: Float = 0f,
    val hasLocationPermission: Boolean = false,
    val hasSensors: Boolean = true,
    val isLoadingLocation: Boolean = false
)
