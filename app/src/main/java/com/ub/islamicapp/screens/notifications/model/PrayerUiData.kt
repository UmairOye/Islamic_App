package com.ub.islamicapp.screens.notifications.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class PrayerUiData(
    val name: String,
    val subtitle: String,
    @DrawableRes val iconResId: Int,
    val bgColor: Color,
    val iconColor: Color,
    val initialOption: String = "Adhan"
)
