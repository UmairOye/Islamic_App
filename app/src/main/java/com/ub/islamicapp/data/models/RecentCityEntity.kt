package com.ub.islamicapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_cities")
data class RecentCityEntity(
    @PrimaryKey val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
